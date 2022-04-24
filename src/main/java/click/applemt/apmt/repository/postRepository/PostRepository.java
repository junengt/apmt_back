package click.applemt.apmt.repository.postRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Post;
import click.applemt.apmt.domain.post.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    /**
     * 판매중인 판매글을 모두 가져온다
     * @return 판매 글 리스트
     */
    @Query("SELECT p FROM Post p WHERE p.deleted = false")
    List<Post> findAll();

    /**
     * 검색 키워드에 맞는 판매글을 모두 가져온다
     * @param searchKeyword 검색 키워드
     * @return 검색 워드에 맞는 판매글 리스트
     */
    @Query("SELECT p  FROM Post p  WHERE p.deleted = false AND p.title LIKE %:searchKeyword%")
    List<Post> findPostsBySearch(String searchKeyword);

    /**
     * postId에 해당하는 판매글을 삭제 상태로 변경한다
     * @param postId    삭제할 판매글의 ID
     */
    @Modifying
    @Query("UPDATE Post p SET p.deleted = true WHERE p.id = :postId")
    void updatePostDelete(@Param("postId") Long postId);

    /**
     * uid에 해당하는 User가 판매중인 판매 글 리스트를 가져온다
     * @param uid   User의 uid
     * @return User의 판매글 리스트
     */
    @Query("SELECT p from Post p join fetch p.user u WHERE p.deleted=false AND u.uid = :uid")
    List<Post> findPostsByUserSelling(String uid);

    /**
     * uid에 해당하는 User의 구매내역을 가져온다
     * @param uid   User의 uid
     * @return  User의 구매내역
     */
    @Query("SELECT t from TradeHistory t join fetch t.user u WHERE u.uid = :uid")
    List<TradeHistory> findPostsByBuying(String uid);


    /**
     * postId에 해당하는 판매글에서 판매자의 user ID를 가져온다
     * @param postId    판매글의 ID
     * @return  판매자의 user ID
     */
    @Query("SELECT p.user.uid FROM Post p WHERE p.deleted=false AND p.id = :postId")
    String getUserIdByPostId(Long postId);

    // 구현중
    @Query("SELECT r FROM Review r")
    List<Review> findReviewsByUid(String uid);

}
