package ulak.jwt.reqres;

import java.util.Set;
import javax.validation.constraints.NotBlank;

public class ExtendRoleRequest {

  @NotBlank
  private String name;
  private Set<String> inheriting;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getInheriting() {
    return inheriting;
  }

  public void setInheriting(Set<String> inheriting) {
    this.inheriting = inheriting;
  }
}
