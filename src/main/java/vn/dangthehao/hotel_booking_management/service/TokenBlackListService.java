package vn.dangthehao.hotel_booking_management.service;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.model.InvalidToken;
import vn.dangthehao.hotel_booking_management.repository.InvalidTokenRepository;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class TokenBlackListService {
  InvalidTokenRepository invalidTokenRepository;
  JwtUtil jwtUtil;

  public void revokeAccessToken(String token) {
    Map<String, Object> claims = jwtUtil.getClaims(token);
    LocalDateTime expiredTime = jwtUtil.getExpiredTime(claims);
    String accTokenID = jwtUtil.getTokenID(claims);
    InvalidToken invalidToken =
        InvalidToken.builder().id(accTokenID).token(token).expiredTime(expiredTime).build();
    invalidTokenRepository.save(invalidToken);
  }

  public boolean isRevoked(String token) {
    Map<String, Object> claims = jwtUtil.getClaims(token);
    return invalidTokenRepository.existsById(jwtUtil.getTokenID(claims));
  }
}
