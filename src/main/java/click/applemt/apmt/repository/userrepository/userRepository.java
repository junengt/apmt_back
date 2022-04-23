package click.applemt.apmt.repository.userRepository;

import click.applemt.apmt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUid(String uid);
=======
@Repository
public interface UserRepository extends JpaRepository<User,String> {
>>>>>>> parent of 559ef68 (++)
}
