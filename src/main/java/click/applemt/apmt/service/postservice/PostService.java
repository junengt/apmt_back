package click.applemt.apmt.service.postservice;

import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.repository.postrepository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

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

    @Data
    @AllArgsConstructor
    public class PostListDto {
        private Long id;
        private String title;
        private int price;
        private String content;
    }
}
