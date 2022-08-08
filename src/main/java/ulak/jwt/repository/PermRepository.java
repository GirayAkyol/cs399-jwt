package ulak.jwt.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ulak.jwt.models.Permission;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface PermRepository extends JpaRepository<Permission, Long> {

  @Transactional(isolation = Isolation.SERIALIZABLE)
  List<Permission> findAllByResource(String resource);
}
