package click.applemt.apmt.service;

import click.applemt.apmt.config.FirebaseInit;
import click.applemt.apmt.controller.post.PostReqDto;
import click.applemt.apmt.controller.post.PostSearchCondition;
import click.applemt.apmt.controller.post.PostUpdateForm;
import click.applemt.apmt.controller.post.PostUpdateReqDto;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.PostRepository;
import click.applemt.apmt.repository.postRepository.PostsPhotoRepository;
import click.applemt.apmt.repository.postRepository.TagRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.util.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostsPhotoRepository postsPhotoRepository;
    private final FirebaseInit firebaseInit;

    //검색어가 없다면 모든 목록 or 검색어가 있다면 검색어에 맞는 목록 노출
    public List<PostListDto> findAllPostAndSearchKeyword(PostSearchCondition searchCond) {
        return postRepository.findPostsBySearch(searchCond).stream()
                .map(p -> new PostListDto(p.getId(), Time.calculateTime(Timestamp.valueOf(p.getCreatedTime())), isEmpty(p.getPhotoList()) ? "https://firebasestorage.googleapis.com/v0/b/applemart-eeb42.appspot.com/o/6CIDfWMwFrQwJgt3FEy3zoGijU63%2F3b5c9b59-4b47-4ae2-85fe-2f4983a097c3?alt=media&token=e763815b-a33c-4656-82fb-b0113f6a6423" : p.getPhotoList().get(0).getPhotoPath(), p.getTitle(), p.getPrice(), p.getContent(), p.getTown(), p.getStatus()))
                .collect(Collectors.toList());
    }

    public PostDto findOne(Long postId, FirebaseToken decodedToken) throws FirebaseAuthException {
        Post findPost = postRepository.findById(postId).get();
        String uid = findPost.getUser().getUid();
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);

        PostDto postDto = new PostDto();
        postDto.setContent(findPost.getContent());
        postDto.setAfterDate(Time.calculateTime(Timestamp.valueOf(findPost.getCreatedTime())));
        postDto.setCreatorId(uid);
        postDto.setCreatorName(user.getDisplayName());
        postDto.setProfileImg(user.getPhotoUrl());
        postDto.setPhotoList(findPost.getPhotoList());
        postDto.setTags(findPost.getTags().stream().map(e -> e.getName()).toList());
        postDto.setTitle(findPost.getTitle());
        if (decodedToken != null)
            postDto.setOwner(decodedToken.getUid().equals(uid));
        postDto.setStatus(findPost.getStatus());
        postDto.setId(findPost.getId());
        postDto.setRegion(findPost.getTown());
        postDto.setPrice(findPost.getPrice());
        postDto.setView(findPost.getView());

        return postDto;

    }

    //Post 조회수 증가 로직
    @Transactional
    public Long updateView(Long postId) {
        return postRepository.updateView(postId);
    }

    //Post삭제 (실제로는 delete가 아니라 update(삭제 플래그 값을 Y로 업데이트함))
    @Transactional
    public Long deleteByPostId(Long postId, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (findPost.getUser().getUid().equals(authUser.getUid())) {
            postRepository.updatePostDelete(postId);
        }
        return postId;
    }

    //Post를 등록하는 로직
    @Transactional
    public Long savePost(PostReqDto postReqDto, AuthUser authUser) {
        User findUser = userRepository.findById(authUser.getUid()).orElseThrow();
        Post post = new Post();
        post.setUser(findUser);
        post.setTown(postReqDto.getTown());
        post.setTitle(postReqDto.getTitle());
        post.setPrice(postReqDto.getPrice());
        post.setContent(postReqDto.getContent());
        List<Tag> tags = tagRepository.findByName(postReqDto.getTags());
        post.setTags(tags);
        post.setStatus(TradeStatus.ING);
        postRepository.save(post);
        return post.getId();
    }

    //Post를 등록할 때 중간에 PostsPhoto 저장하는 로직
    @Transactional
    public void savePostPhotos(Long postId, AuthUser authUser, List<MultipartFile> files) {
        //입력된 이미지가 있다면 savePost메서드에서 저장된 게시물을 찾음
        Post findPost = postRepository.findById(postId).orElseThrow();
        //사용자 인증 정보가 일치하지 않으면 이 메서드는 실행하지 않음 -> 에러처리로 바꾸든 없애든 해야함
        if (!findPost.getUser().getUid().equals(authUser.getUid())) {
            return;
        }
        //Post를 수정할 때 이미지가 이미 존재하고 이미지 저장을 하지 않는다면 레파지토리에서 삭제함
        if (!findPost.getPhotoList().isEmpty() && CollectionUtils.isEmpty(files)) {
            postsPhotoRepository.deleteByPostId(postId);
        }
        //Post를 수정할때 이미지가 이미 존재한다면 레파지토리에서 삭제함
        if (!findPost.getPhotoList().isEmpty()) {
            postsPhotoRepository.deleteByPostId(postId);
        }
        //Post를 등록할 때 이미지가 없으면 이 메서드는 실행하지 않음
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        for (MultipartFile file : files) {
            //하나의 게시물을 참조하는 이미지 하나 생성 (루프 돌면서 복수의 이미지 넣기)
            //이미지에 랜덤 UUID 생성해서 집어넣음으로 이미지 덮어쓰임 방지
            String uuid = UUID.randomUUID().toString();
            String absolPath = new File("").getAbsolutePath() + "\\";
            String testPath = "images/";
            //filePath 수정해야함
            String imagePath = testPath + uuid + file.getOriginalFilename();
            PostsPhoto postsPhoto = PostsPhoto.builder().photoPath(imagePath).post(findPost).build();
            //파일을 서버 저장소에 저장
            try {
                file.transferTo(new File(absolPath + imagePath));
                Files.copy(file.getInputStream(), Path.of(absolPath + imagePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println(e);
                //콘솔 출력 말고 log출력으로..
            }
            //파일 저장 끝
            postsPhotoRepository.save(postsPhoto);
        }
    }


    //Post 수정하는 로직
    @Transactional
    public Long updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (findPost.getUser().getUid().equals(authUser.getUid())) {
            findPost.setTown(postUpdateReqDto.getTown());
            findPost.setTitle(postUpdateReqDto.getTitle());
            findPost.setPrice(postUpdateReqDto.getPrice());
            findPost.setContent(postUpdateReqDto.getContent());
            List<Tag> tags = tagRepository.findByName(postUpdateReqDto.getTags());
            findPost.setTags(tags);
            findPost.setStatus(TradeStatus.ING);
            postRepository.save(findPost);
        }
        return findPost.getId();
    }


    //수정할 Post 불러오는 로직
    @Transactional
    public PostUpdateForm findPostForm(Long postId, AuthUser authUser) {
        Post findPost = postRepository.findById(postId).get();
        if (!findPost.getUser().getUid().equals(authUser.getUid())) {
            return null;
        }
        PostUpdateForm form = new PostUpdateForm();
        form.setPrice(findPost.getPrice());
        form.setContent(findPost.getContent());
        form.setTags(findPost.getTags().stream().map(e -> e.getName()).toList());
        form.setTown(findPost.getTown());
        form.setPostsPhotos(findPost.getPhotoList());
        form.setTitle(findPost.getTitle());
        return form;
    }

    @Data
    @AllArgsConstructor
    public class PostListDto {
        private Long id;
        private String afterDate;
        private String img;
        private String title;
        private Long price;
        private String content;
        private String Region;
        private TradeStatus status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class PostDto {
        private Long id;
        private String creatorId;
        private String profileImg;
        private String creatorName;
        private String afterDate;
        private List<PostsPhoto> photoList;
        private String title;
        private Long price;
        private String content;
        private String Region;
        private TradeStatus status;
        private boolean isOwner;
        private List<String> tags;
        private Integer view;
    }
}
