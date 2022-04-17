package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.Post;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findAll();
    List<Post> findPostsBySearch(String searchKeyword);
    void updatePostDelete(@Param("postId") Long postId);
}
