package vn.dangthehao.hotel_booking_management.service;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.model.InvalidToken;
import vn.dangthehao.hotel_booking_management.repository.InvalidTokenRepository;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.util.TimeConverter;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TokenBlackListService {
  InvalidTokenRepository invalidTokenRepository;
  JwtService jwtService;

  public void revokeAccessToken(Jwt accessJwt) {
    LocalDateTime expiredTime = jwtService.getJwtExpiration(accessJwt);
    LocalDateTime now = TimeConverter.instantToLocalDateTime(Instant.now());

    if (expiredTime.isAfter(now)) {
      String jti = jwtService.getJwtId(accessJwt);
      String value = jwtService.getValue(accessJwt);
      var token = InvalidToken.builder().id(jti).token(value).expiredTime(expiredTime).build();
      invalidTokenRepository.save(token);
    }
  }

  public boolean isRevoked(String token) {
    return invalidTokenRepository.existsById(jwtService.getJwtId(token));
  }
}
