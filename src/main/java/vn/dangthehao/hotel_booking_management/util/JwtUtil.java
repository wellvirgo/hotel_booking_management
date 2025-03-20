package vn.dangthehao.hotel_booking_management.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.security.CustomDecoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

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

    public String getTokenID(Map<String, Object> claims) {
        return (String) claims.get("jti");
    }

    public Long getUserID(Map<String, Object> claims) {
        return (Long) claims.get("userID");
    }
}
