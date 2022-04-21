package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.controller.post.PostSearchCondition;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.security.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> findPostsBySearch(PostSearchCondition searchCond, Pageable pageable);
    void updatePostDelete(@Param("postId") Long postId);
}
