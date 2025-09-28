package vn.dangthehao.hotel_booking_management.security;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.util.TimeConverter;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class JwtService {
  CustomDecoder customDecoder;

  private Jwt decode(String token) {
    try {
      return customDecoder.decode(token);
    } catch (JwtException e) {
      throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
  }

  public Long getUserId(String token) {
    return Long.parseLong(decode(token).getSubject());
  }

  public Long getUserId(Jwt jwt) {
    return Long.parseLong(jwt.getSubject());
  }

  public String getJwtId(String token) {
    return decode(token).getId();
  }

  public String getJwtId(Jwt jwt) {
    return jwt.getId();
  }

  public LocalDateTime getJwtExpiration(String token) {
    Jwt jwt = decode(token);
    return TimeConverter.instantToLocalDateTime(jwt.getExpiresAt());
  }

  public LocalDateTime getJwtExpiration(Jwt jwt) {
    return TimeConverter.instantToLocalDateTime(jwt.getExpiresAt());
  }

  public String getValue(Jwt jwt) {
    return jwt.getTokenValue();
  }

  public int getVersion(String token) {
    return decode(token).getClaim("version");
  }
}
