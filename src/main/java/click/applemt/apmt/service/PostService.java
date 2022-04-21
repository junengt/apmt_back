package click.applemt.apmt.service;

import click.applemt.apmt.config.FirebaseInit;
import click.applemt.apmt.controller.post.PostReqDto;
import click.applemt.apmt.controller.post.PostSearchCondition;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.TableGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                .map(p -> new PostListDto(p.getId(), Time.calculateTime(Timestamp.valueOf(p.getCreatedTime())), p.getPhotoList().get(0).getPhotoPath(), p.getTitle(), p.getPrice(), p.getContent(), p.getTown(), p.getStatus()))
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

        return postDto;

    }

    //Post삭제 (실제로는 delete가 아니라 update(삭제 플래그 값을 Y로 업데이트함))
    @Transactional
    public Long deleteByPostId(Long postId) {
        postRepository.updatePostDelete(postId);
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
    public void savePostPhotos(Long postId, List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        Post findPost = postRepository.findById(postId).orElseThrow();
        //.get 말고 orElseThrow로 에러 처리
        for (MultipartFile file : files) {
            //하나의 게시물을 참조하는 이미지 하나 생성 (루프 돌면서 복수의 이미지 넣기)
            String filePath = "C:\\Users\\kaas1\\Downloads\\" + file.getOriginalFilename();
            //filePath 수정해야함
            PostsPhoto postsPhoto = PostsPhoto.builder().photoPath(filePath).post(findPost).build();
            //파일을 서버 저장소에 저장
            try {
                Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println(e);
                //콘솔 출력 말고 log출력으로..
            }
            //파일 저장 끝
            postsPhotoRepository.save(postsPhoto);
        }
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
    }
}
