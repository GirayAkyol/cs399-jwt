package ulak.jwt.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ulak.jwt.models.CustomUser;
import ulak.jwt.models.Role;
import ulak.jwt.repository.RoleRepository;
import ulak.jwt.repository.UserRepository;
import ulak.jwt.reqres.JwtResponse;
import ulak.jwt.reqres.SignInRequest;
import ulak.jwt.reqres.MessageResponse;
import ulak.jwt.reqres.SignUpRequest;
import ulak.jwt.security.jwt.JwtUtility;
import ulak.jwt.security.service.UserDetailsConc;

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
  PasswordEncoder encoder;

  @Autowired
  JwtUtility jwtUtils;


  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsConc userDetails = (UserDetailsConc) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }


    CustomUser user = new CustomUser(signUpRequest.getUsername(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName("ROLE_USER")
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        if (role.equals("admin")){
          Role adminRole = roleRepository.findByName("ROLE_ADMIN")
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

        }else if(role.equals("mod")){
          Role modRole = roleRepository.findByName("ROLE_MODERATOR")
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);
        }else if(role.equals("user")) {
          Role userRole = roleRepository.findByName("ROLE_USER")
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }

      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
