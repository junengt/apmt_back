package click.applemt.apmt.repository;

import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Review;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

    @Query("SELECT r FROM Review r JOIN FETCH r.tradeHistory t WHERE t.id = :tradeHistoryId")
    List<Review> getReviewsByTradeHistoryId(Long tradeHistoryId);

}
