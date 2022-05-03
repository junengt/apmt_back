package click.applemt.apmt.repository.reviewRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Review;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    /**
     * TradeHistory ID를 입력하면 Review 리스트를 반환한다
     * @param tradeHistoryId TradeHistory ID 거래내역의 ID
     * @return 후기 내역
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.tradeHistory t WHERE t.id = :tradeHistoryId")
    List<Review> getReviewsByTradeHistoryId(Long tradeHistoryId);


    /**
     * 판매자의 User ID를 입력하면 판매자의 거래후기 목록을 반환한다
     * @param uid 판매자의 User ID
     * @return 판매자의 거래 후기 내역
     */
    @Query("SELECT r FROM Review r WHERE r.sellerUid = :uid")
    List<Review> getReviewsBySellerUid(String uid);