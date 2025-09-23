package vn.dangthehao.hotel_booking_management.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
  public static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public static boolean isLoggedIn() {
    Authentication auth = getAuthentication();
    return auth != null
        && auth.isAuthenticated()
        && !(auth instanceof AnonymousAuthenticationToken);
  }

  public static void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }
}
