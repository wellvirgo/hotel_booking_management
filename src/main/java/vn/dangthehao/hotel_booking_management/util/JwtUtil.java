package vn.dangthehao.hotel_booking_management.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.security.CustomDecoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtUtil {
    CustomDecoder customDecoder;

    public Map<String, Object> getClaims(String token) {
        return customDecoder.decode(token).getClaims();
    }

    public LocalDateTime getExpiredTime(Map<String, Object> claims) {
        Instant expiredTime = (Instant) claims.get("exp");
        return TimeConverter.instantToLocalDateTime(expiredTime);
    }

    public LocalDateTime getExpiredTime(Jwt jwt) {
        return TimeConverter.instantToLocalDateTime(Objects.requireNonNull(jwt.getExpiresAt()));
    }

    public String getTokenID(Map<String, Object> claims) {
        return (String) claims.get("jti");
    }

    public Long getUserID(Map<String, Object> claims) {
        return (Long) claims.get("userID");
    }

    public Long getUserID(Jwt jwt) {
        return jwt.getClaim("userID");
    }
}
