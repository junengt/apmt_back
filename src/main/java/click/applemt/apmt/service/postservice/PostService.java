package click.applemt.apmt.service.postservice;

import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.PostsPhoto;
import click.applemt.apmt.repository.postrepository.PostRepository;
import click.applemt.apmt.repository.postrepository.PostTagRepository;
import click.applemt.apmt.repository.postrepository.PostsPhotoRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private final PostRepository postRepository;
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

    public void deleteByPostId(Long postId) {
        postRepository.updatePostDelete(postId);
    }

    //Post를 등록할 때 중간에 PostsPhoto 저장하는 로직
    @Transactional
    public void savePostPhotos(Long postId, List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        Post post = postRepository.findById(postId).get();
        //.get 말고 orElseThrow로 에러 처리
        for (MultipartFile file : files) {
            //하나의 게시물을 참조하는 이미지 하나 생성 (루프 돌면서 복수의 이미지 넣기)
            String filePath = "C:\\Users\\kaas1\\Downloads\\" + file.getOriginalFilename();
            //filePath 수정해야함
            PostsPhoto postsPhoto = PostsPhoto.builder().photoPath(filePath).post(post).build();
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
        private String title;
        private int price;
        private String content;
    }
}
