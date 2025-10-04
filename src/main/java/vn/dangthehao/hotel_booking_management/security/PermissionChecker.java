package vn.dangthehao.hotel_booking_management.security;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.model.Permission;
import vn.dangthehao.hotel_booking_management.model.Role;
import vn.dangthehao.hotel_booking_management.service.RoleService;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionChecker {
  RoleService roleService;

  public boolean hasAuthorities(List<String> permissions) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String roleName = getRoleName(authentication);
    Role role = roleService.getByRoleName(roleName);

    return role.getPermissions().stream()
        .map(Permission::getPermissionName)
        .anyMatch(permissions::contains);
  }

  private String getRoleName(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining());
  }
}
