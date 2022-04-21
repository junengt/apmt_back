package click.applemt.apmt.repository.tradeHistroyRepository;

import click.applemt.apmt.domain.point.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
}
