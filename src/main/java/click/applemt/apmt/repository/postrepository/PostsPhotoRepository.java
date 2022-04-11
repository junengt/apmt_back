package click.applemt.apmt.repository.postrepository;

import click.applemt.apmt.domain.post.PostsPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsPhotoRepository extends JpaRepository<PostsPhoto, Long> {
}
