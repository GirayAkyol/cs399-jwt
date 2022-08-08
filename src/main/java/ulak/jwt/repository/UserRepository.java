package ulak.jwt.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ulak.jwt.models.CustomUser;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface UserRepository extends JpaRepository<CustomUser, Long> {

  @Transactional(isolation = Isolation.SERIALIZABLE)
  Optional<CustomUser> findByUsername(String username);

  @Transactional(isolation = Isolation.SERIALIZABLE)
  Boolean existsByUsername(String username);

}