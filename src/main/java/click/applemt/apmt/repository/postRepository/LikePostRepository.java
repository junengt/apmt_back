package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.post.LikePost;
import click.applemt.apmt.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Long> {
    Optional<LikePost> findByUserAndPost(User user, Post post);
}
