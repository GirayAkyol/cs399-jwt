package ulak.jwt.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ulak.jwt.models.Role;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)

public interface RoleRepository extends JpaRepository<Role, Long> {

  @Transactional(isolation = Isolation.SERIALIZABLE)
  Optional<Role> findByName(String name);
}
