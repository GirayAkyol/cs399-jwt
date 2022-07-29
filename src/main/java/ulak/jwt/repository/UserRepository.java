package ulak.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ulak.jwt.models.CustomUser;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {
  Optional<CustomUser> findByUsername(String username);

  Boolean existsByUsername(String username);

}