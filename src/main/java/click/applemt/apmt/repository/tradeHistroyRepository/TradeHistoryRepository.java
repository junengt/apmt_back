package click.applemt.apmt.repository.tradeHistroyRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import click.applemt.apmt.domain.post.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
    /**
     * userID에 해당하는 User의 거래내역을 가져온다
     * @param uid    User의 ID
     * @return  User의 거래 내역 목록
     */
    @Query("SELECT t FROM TradeHistory t ")
    List<TradeHistory> findTradeHistoriesByUid(String uid);
    /**
     * 거래내역 ID에 해당하는 리뷰들의 ID를 가져온다
     * @param tradeHistoryId
     * @return
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.tradeHistory t WHERE t.id = :tradeHistoryId")
    List<Review> findReviewsByTradeHistoryId(Long tradeHistoryId);
/*

    SELECT R.REVIEW_ID , R.REVIEW_CONTENT , R.CREATED_AT , R. UPDATED_AT , T.USER_ID  FROM REVIEW AS R

    LEFT JOIN TRADE_HISTORY AS T

    WHERE R.TRADE_HISTORY_ID = T.TRADE_HISTORY_ID AND T.USER_ID = 'QkWS3G5rC5dRG59yTWSuRjWBm0n2'
*/

}
