package ulak.jwt.models;

import javax.persistence.*;

@Entity
@Table(name = "perms")
public class Permission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String resource;
  private String action;


  public Permission() {

  }

  public Permission(String resource, String action) {
    this.resource = resource;
    this.action = action;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }
}
