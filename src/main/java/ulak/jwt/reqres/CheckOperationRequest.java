package ulak.jwt.reqres;

import javax.validation.constraints.NotBlank;

public class CheckOperationRequest {

  @NotBlank
  private String jwtToken;
  @NotBlank
  private String action;

  public String getJwtToken() {
    return jwtToken;
  }

  public void setJwtToken(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
