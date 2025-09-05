package vn.dangthehao.hotel_booking_management.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
  Authentication getAuthentication();
}
