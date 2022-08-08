package ulak.jwt.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ulak.jwt.models.CustomUser;
import ulak.jwt.models.Role;
import ulak.jwt.repository.PermRepository;
import ulak.jwt.repository.RoleRepository;
import ulak.jwt.repository.UserRepository;
import ulak.jwt.reqres.JwtResponse;
import ulak.jwt.reqres.MessageResponse;
import ulak.jwt.reqres.SignInRequest;
import ulak.jwt.reqres.SignUpRequest;
import ulak.jwt.security.jwt.JwtUtility;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PermRepository permRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtility jwtUtils;


  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
            signInRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
  }

  @PostMapping("/signup")
//  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    CustomUser user = new CustomUser(signUpRequest.getUsername(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      // Default to user role.
      Role userRole = roleRepository.findByName("ROLE_USER")
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(roleCandidate -> {
        // Canonicalize role name with fixed locale.
        Role role = roleRepository.findByName("ROLE_" + roleCandidate.toUpperCase(Locale.ROOT))
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(true);
  }
}
