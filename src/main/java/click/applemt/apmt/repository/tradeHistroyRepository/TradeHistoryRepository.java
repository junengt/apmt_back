package click.applemt.apmt.repository.tradeHistroyRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
    /**
     * userID에 해당하는 User의 거래내역을 가져온다
     * @param uid    User의 ID
     * @return  User의 거래 내역 목록
     */
    @Query("SELECT t FROM TradeHistory t WHERE t.post.user.uid = :uid")
    List<TradeHistory> getTradeHistoriesByUserUid(String uid);

/*

    SELECT R.REVIEW_ID , R.REVIEW_CONTENT , R.CREATED_AT , R. UPDATED_AT , T.USER_ID  FROM REVIEW AS R

    LEFT JOIN TRADE_HISTORY AS T

    WHERE R.TRADE_HISTORY_ID = T.TRADE_HISTORY_ID AND T.USER_ID = 'QkWS3G5rC5dRG59yTWSuRjWBm0n2'
*/

}
