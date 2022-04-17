package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.post.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
