package click.applemt.apmt.repository.userRepository;


import click.applemt.apmt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findByUid(String uid);

}

