package ulak.jwt.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "crole")
@Transactional(isolation = Isolation.SERIALIZABLE)
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToMany
  @JoinTable(name = "roles_roles", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "inherits_id", referencedColumnName = "id"))
  private Set<Role> inherits = new HashSet<>();

  //  @ManyToMany(mappedBy = "inherits")
//  private Set<Role> inheritedBy;
  @ManyToMany
  @JoinTable(name = "roles_perms", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "perm_id", referencedColumnName = "id"))
  private Set<Permission> immediatePermissions = new HashSet<>();

  @ManyToMany(mappedBy = "roles")
  private Set<CustomUser> users;

  public Role() {

  }

  public Role(String name) {
    this.name = name;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public boolean canInheritRole(Role toBeInherited) {
    Set<Role> roles = toBeInherited.getDirectlyOrIndirectlyInheritedRoles();
    return !roles.contains(this);

  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public boolean inheritRole(Role r) {
    if (canInheritRole(r)) {
      return inherits.add(r);
    } else {
      return false;
    }
  }


  /**
   * Calculates reflexive graph closure of the inherited roles. Planned to be used for cycle
   * detection when being inherited by other roles. Infinite recursion on cycles.
   *
   * @return Set of all roles that are directly or indirectly reachable.
   */
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Set<Role> getDirectlyOrIndirectlyInheritedRoles() {
    Set<Role> reachable = new HashSet<>(Collections.emptySet());
    for (Role r : inherits) {
      reachable.addAll(r.getDirectlyOrIndirectlyInheritedRoles());
    }
    reachable.addAll(inherits); // probably not required
    reachable.add(this);
    return reachable;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Role role = (Role) o;
    return Objects.equals(id, role.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public Set<Permission> getImmediatePermissions() {
    return immediatePermissions;
  }

  public void setImmediatePermissions(Set<Permission> immediatePermissions) {
    this.immediatePermissions = immediatePermissions;
  }

  public Set<Role> getInherits() {
    return inherits;
  }

}
