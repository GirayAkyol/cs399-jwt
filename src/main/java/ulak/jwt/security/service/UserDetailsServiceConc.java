package ulak.jwt.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ulak.jwt.models.CustomUser;
import ulak.jwt.models.Permission;
import ulak.jwt.models.Role;
import ulak.jwt.repository.RoleRepository;
import ulak.jwt.repository.UserRepository;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class UserDetailsServiceConc implements UserDetailsService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Override
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    CustomUser user = userRepository.findByUsername(username).orElseThrow(
        () -> new UsernameNotFoundException("User Not Found with username: " + username));

    return User.builder().username(user.getUsername()).password(user.getPassword())
        .authorities(getGrantedAuthorities(getPrivileges(user.getRoles()))).build();
  }

  private Set<String> getPrivileges(Collection<Role> roles) {

    Set<String> privileges = new HashSet<>();
    Set<Permission> collection = new HashSet<>();
    for (Role role : roles) {
      privileges.add(role.getName());
      collection.addAll(role.getImmediatePermissions());
    }
    for (Permission item : collection) {
      privileges.add(item.toString());
    }
    return privileges;
  }

  private Set<GrantedAuthority> getGrantedAuthorities(Set<String> privileges) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    for (String privilege : privileges) {
      authorities.add(new SimpleGrantedAuthority(privilege));
    }
    return authorities;
  }
}
