package ulak.jwt.security.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ulak.jwt.models.Role;
import ulak.jwt.repository.RoleRepository;

@Service
public class RoleHierarchyConc implements RoleHierarchy {

  @Autowired
  RoleRepository roleRepository;

  @Override
  public Collection<? extends GrantedAuthority> getReachableGrantedAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    if (authorities == null || authorities.isEmpty()) {
      return Collections.emptyList();
    } else {
      Set<GrantedAuthority> reachable = new HashSet<>();
      for (GrantedAuthority grantedAuthority : authorities) {
        Optional<Role> or = roleRepository.findByName(grantedAuthority.getAuthority());
        if (or.isPresent()) {
          Set<Role> roles = or.get().getDirectlyOrIndirectlyInheritedRoles();
          for (Role r : roles) {
            reachable.add(new SimpleGrantedAuthority(r.getName()));
          }
        }
      }
      return reachable;
    }
  }
}
