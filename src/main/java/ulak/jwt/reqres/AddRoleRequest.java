package ulak.jwt.reqres;

import java.util.Set;
import javax.validation.constraints.NotBlank;

public class AddRoleRequest {

  @NotBlank
  private String name;

  private Set<String> permissions;

  private Set<String> inheriting;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  public Set<String> getInheriting() {
    return inheriting;
  }

  public void setInheriting(Set<String> inheriting) {
    this.inheriting = inheriting;
  }
}
