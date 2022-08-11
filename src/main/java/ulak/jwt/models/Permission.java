package ulak.jwt.models;

import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "cperm")
@Transactional(isolation = Isolation.SERIALIZABLE)
public class Permission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String resource;

  public EnumAction getAction() {
    return action;
  }

  public void setAction(EnumAction action) {
    this.action = action;
  }

  @Enumerated(EnumType.STRING)
  private EnumAction action;

  @ManyToMany(mappedBy = "immediatePermissions")
  private Set<Role> roles;


  public Permission() {

  }

  public Permission(String resource, EnumAction action) {
    this.resource = resource;
    this.action = action;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Permission that = (Permission) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    String out = resource + "#";
    if (this.action.equals(EnumAction.ACTION_CREATE)) {
      out = out + "CREATE";
    } else if (this.action.equals(EnumAction.ACTION_DELETE)) {
      out = out + "DELETE";
    } else if (this.action.equals(EnumAction.ACTION_READ)) {
      out = out + "READ";
    } else if (this.action.equals(EnumAction.ACTION_WRITE)) {
      out = out + "WRITE";
    }
    return out;
  }
}
