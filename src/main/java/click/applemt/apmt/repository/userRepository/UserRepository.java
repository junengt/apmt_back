package click.applemt.apmt.repository.userrepository;

import click.applemt.apmt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface userRepository extends JpaRepository<User, Long> {
    List<User> findById(String id);
}
