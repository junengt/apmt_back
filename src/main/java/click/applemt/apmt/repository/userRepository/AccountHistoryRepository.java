package click.applemt.apmt.repository.userRepository;

import click.applemt.apmt.domain.point.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistory,Long> {

    @Query("select ac from AccountHistory ac where ac.user.uid = :uid order by ac.createdTime desc ")
    List<AccountHistory> findByUserId(String uid);

}
