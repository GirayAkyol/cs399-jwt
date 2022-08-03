package ulak.jwt.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ulak.jwt.models.Permission;

@Repository
public interface PermRepository extends JpaRepository<Permission, Long> {
  List<Permission> findAllByResource(String resource);
}
