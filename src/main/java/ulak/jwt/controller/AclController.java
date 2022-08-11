package ulak.jwt.controller;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ulak.jwt.models.CustomUser;
import ulak.jwt.models.EnumAction;
import ulak.jwt.models.Permission;
import ulak.jwt.models.Role;
import ulak.jwt.repository.PermRepository;
import ulak.jwt.repository.RoleRepository;
import ulak.jwt.repository.UserRepository;
import ulak.jwt.reqres.AddRoleRequest;
import ulak.jwt.reqres.CheckOperationRequest;
import ulak.jwt.reqres.ExtendRoleRequest;
import ulak.jwt.reqres.MessageResponse;
import ulak.jwt.security.jwt.JwtUtility;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/acl")
public class AclController {

  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PermRepository permRepository;

  @Autowired
  JwtUtility jwtUtility;

  @PostMapping("/add/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addRole(@Valid @RequestBody AddRoleRequest addRoleRequest) {
    if (roleRepository.existsByName("ROLE_" + addRoleRequest.getName().toUpperCase(Locale.ROOT))) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Role name is already taken!"));
    }
    Role newRole = new Role("ROLE_" + addRoleRequest.getName().toUpperCase(Locale.ROOT));

    {
      Set<String> strRoles = addRoleRequest.getInheriting();
      if (strRoles != null) {
        strRoles.forEach(roleCandidate -> {
          // Canonicalize role name with fixed locale.
          roleCandidate = "ROLE_" + roleCandidate.toUpperCase(Locale.ROOT);
          Role role = roleRepository.findByName(roleCandidate)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          newRole.inheritRole(role);
        });
      }
    }
    Set<String> strPerms = addRoleRequest.getPermissions();
    if (strPerms != null) {
      strPerms.forEach(permCandidate -> {
        String res = permCandidate.split("#")[0];
        String action = permCandidate.split("#")[1];
        switch (action) {
          case "CREATE" -> newRole.addPermission(new Permission(res, EnumAction.ACTION_CREATE));
          case "DELETE" -> newRole.addPermission(new Permission(res, EnumAction.ACTION_DELETE));
          case "READ" -> newRole.addPermission(new Permission(res, EnumAction.ACTION_READ));
          case "WRITE" -> newRole.addPermission(new Permission(res, EnumAction.ACTION_WRITE));
        }
      });
    }

    permRepository.saveAll(newRole.getImmediatePermissions());
    roleRepository.save(newRole);

    return ResponseEntity.ok(true);
  }


  @PostMapping("/extend/role")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> extendRole(@Valid @RequestBody ExtendRoleRequest extendRoleRequest) {

    Role newRole = roleRepository.findByName(
            "ROLE_" + extendRoleRequest.getName().toUpperCase(Locale.ROOT))
        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

    {
      Set<String> strRoles = extendRoleRequest.getInheriting();
      if (strRoles != null) {
        strRoles.forEach(roleCandidate -> {
          // Canonicalize role name with fixed locale.
          roleCandidate = "ROLE_" + roleCandidate.toUpperCase(Locale.ROOT);
          Role role = roleRepository.findByName(roleCandidate)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          boolean added = newRole.inheritRole(role);
          if (!added) {
            throw new RuntimeException("Error: Role is not inheritable.");
          }
        });
      }

      roleRepository.save(newRole);

      return ResponseEntity.ok(true);
    }
  }


  @GetMapping("/check")
  public ResponseEntity<?> checkOperation(
      @Valid @RequestBody CheckOperationRequest checkOperationRequest) {
    // get identity from jwt
    String jwt = checkOperationRequest.getJwtToken();
    if (jwtUtility.validateJwtToken(jwt)) {
      String username = jwtUtility.getUserNameFromJwtToken(jwt);
      CustomUser user = userRepository.findByUsername(username)
          .orElseThrow(() -> new RuntimeException("User does not exist."));
      Set<Permission> allperms = user.getRoles().stream()
          .map(Role::getDirectlyOrIndirectlyInheritedRoles).flatMap(Set::stream)
          .collect(Collectors.toSet()).stream().map(Role::getImmediatePermissions)
          .flatMap(Set::stream).collect(Collectors.toSet());

      EnumAction operationAction;
      String res = checkOperationRequest.getAction().split("#")[0];
      String action = checkOperationRequest.getAction().split("#")[1];
      switch (action) {
        case "CREATE" -> operationAction = EnumAction.ACTION_CREATE;
        case "DELETE" -> operationAction = EnumAction.ACTION_DELETE;
        case "READ" -> operationAction = EnumAction.ACTION_READ;
        case "WRITE" -> operationAction = EnumAction.ACTION_WRITE;
        default -> throw new IllegalStateException("Unexpected value: " + action);
      }

      Optional<Permission> authorization = allperms.stream()
          .filter(permission -> permission.getAction().equals(operationAction))
          .filter(permission -> res.equals(permission.getResource())).findFirst();
//          .filter(permission -> res.matches(permission.getResource())).findFirst();
      if (authorization.isPresent()) {
        return ResponseEntity.ok(true);
      } else {
        return ResponseEntity.ok(false);
      }
    } else {
      throw new RuntimeException("Jwt is not valid.");
    }
  }
}
