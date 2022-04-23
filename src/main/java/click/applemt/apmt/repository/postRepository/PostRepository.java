package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.LikePost;
import click.applemt.apmt.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT p FROM Post p WHERE p.deleted = false")
    List<Post> findAll();

    @Query("SELECT p  FROM Post p  WHERE p.deleted = false AND p.title LIKE %:searchKeyword%")
    List<Post> findPostsBySearch(String searchKeyword);

    @Modifying
    @Query("UPDATE Post p SET p.deleted = true WHERE p.id = :postId")
    void updatePostDelete(@Param("postId") Long postId);

    @Query("SELECT p from Post p join fetch p.user u WHERE p.deleted=false AND u.uid = :uid ")
    List<Post> findPostsByUserSelling(String uid);

    @Query("SELECT t from TradeHistory t join fetch t.user u WHERE u.uid = :uid")
    List<TradeHistory> findPostsByBuying(String uid);

    @Query("SELECT p from LikePost p join fetch p.user u where u.uid = :uid")
    List<LikePost> findPostsByLike(String uid);

}
