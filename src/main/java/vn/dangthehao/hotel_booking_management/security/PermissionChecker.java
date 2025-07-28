package vn.dangthehao.hotel_booking_management.security;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker {
  public boolean hasAuthorities(List<String> permissions) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(permissions::contains);
  }
}
