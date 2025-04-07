package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisService {
    RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean setTimeToLive(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public void saveOTPWithExpiredTime(String email, String otp) {
        final long expiredTimeOTP = 5;
        String key = email + "_otp";
        set(key, otp);
        setTimeToLive(email, expiredTimeOTP);
    }

    public void saveResetToken(String email, String resetToken) {
        final long expiredTimeToken = 5;
        String key = email + "_resetToken";
        set(key, resetToken);
        setTimeToLive(key, expiredTimeToken);
    }
}
