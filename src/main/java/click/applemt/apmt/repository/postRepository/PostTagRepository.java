package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
