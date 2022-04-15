package click.applemt.apmt.repository.userRepository;

import click.applemt.apmt.domain.point.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory,Long> {

    @Query("select ac from AccountHistory  ac join fetch ac.user u where ac.user.uid = :uid ")
    List<AccountHistory> findByUserId(String uid);

}
