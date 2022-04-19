package click.applemt.apmt.service;

import click.applemt.apmt.controller.postController.PostDto;
import click.applemt.apmt.controller.postController.PostListDto;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.*;
import click.applemt.apmt.repository.postRepository.*;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.TableGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    private final PostTagRepository postTagRepository;

    //검색어가 없다면 모든 목록 or 검색어가 있다면 검색어에 맞는 목록 노출
    public List<PostListDto> findAllPostAndSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return postRepository.findAll().stream()
                    .map(p -> new PostListDto(p.getId(), p.getTitle(), p.getPrice(), p.getContent()))
                    .collect(Collectors.toList());
        } else {
            return postRepository.findPostsBySearch(searchKeyword).stream()
                    .map(p -> new PostListDto(p.getId(), p.getTitle(), p.getPrice(), p.getContent()))
                    .collect(Collectors.toList());
        }
    }

    //Post삭제 (실제로는 delete가 아니라 update(삭제 플래그 값을 Y로 업데이트함))
    public void deleteByPostId(Long postId) {
        postRepository.updatePostDelete(postId);
    }

    //Post를 등록하는 로직
    @Transactional
    public Long savePost(PostDto postDto, AuthUser authUser) {
        User findUser = userRepository.findById(authUser.getUid()).orElseThrow();
        Post post = new Post();
        post.setUser(findUser);
        post.setTitle(postDto.getTitle());
        post.setPrice(postDto.getPrice());
        post.setContent(postDto.getContent());
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

    //Post를 등록할 때 중간에 Tag 저장하는 로직
    @Transactional
    public void savePostTags(Long postId, Long tagId) {
        Post findPost = postRepository.findById(postId).orElseThrow();
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        PostTag postTag = PostTag.builder().post(findPost).tag(tag).build();
        postTagRepository.save(postTag);
    }

}

