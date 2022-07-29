package ulak.jwt.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ulak.jwt.security.service.UserDetailsConc;

@Component
public class JwtUtility {
  @Value("${ulak.jwt.Secret}")
  private String jwtSecret;

  //@Value("${ulak.jwt.ExpirationMiliSec}")
  private final long jwtExpirationMs = ChronoUnit.DAYS.getDuration().toMillis();
  public String generateJwtToken(Authentication authentication) {

    UserDetailsConc userPrincipal = (UserDetailsConc) authentication.getPrincipal();

    return Jwts.builder().setSubject((userPrincipal.getUsername())).
        setIssuedAt( new Date() )
        .setExpiration(new Date((new Date()).getTime() + ChronoUnit.DAYS.getDuration().toMillis())).signWith(
            SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    if (StringUtils.hasText(authToken)){
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
             UnsupportedJwtException | IllegalArgumentException e) {
      System.out.println("Invalid JWT signature: " + e.getMessage());
}
}
    return false;
  }
}
