package ulak.jwt.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ulak.jwt.models.Permission;

@Repository
public interface PermRepository extends JpaRepository<Permission, Long> {
  @Transactional(isolation = Isolation.SERIALIZABLE)
  List<Permission> findAllByResource(String resource);
}
