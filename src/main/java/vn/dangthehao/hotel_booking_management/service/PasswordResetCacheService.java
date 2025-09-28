package vn.dangthehao.hotel_booking_management.service;

import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PasswordResetCacheService {
  RedisService redisService;

  @NonFinal
  @Value("${otp.password-reset.ttl-seconds}")
  long passwordResetOtpTtl;

  @NonFinal
  @Value("${password-reset-token.ttl-seconds}")
  long passwordResetTokenTtl;

  static final String PASSWORD_RESET_OTP_KEY_PATTERN = "auth:otp:password-reset:%s";
  static final String PASSWORD_RESET_TOKEN_KEY_PATTERN = "auth:token:password-reset:%s";

  public void cacheOtp(String email, String otp) {
    String key = String.format(PASSWORD_RESET_OTP_KEY_PATTERN, email);
    redisService.setEx(key, otp, passwordResetOtpTtl, TimeUnit.SECONDS);
  }

  public String getOtp(String email) {
    String key = String.format(PASSWORD_RESET_OTP_KEY_PATTERN, email);
    return (String) redisService.get(key);
  }

  public void deleteOtp(String email) {
    String key = String.format(PASSWORD_RESET_OTP_KEY_PATTERN, email);
    redisService.delete(key);
  }

  public void cacheToken(String email, String token) {
    String key = String.format(PASSWORD_RESET_TOKEN_KEY_PATTERN, email);
    redisService.setEx(key, token, passwordResetTokenTtl, TimeUnit.SECONDS);
  }

  public String getToken(String email) {
    String key = String.format(PASSWORD_RESET_TOKEN_KEY_PATTERN, email);
    return (String) redisService.get(key);
  }

  public void deleteToken(String email) {
    String key = String.format(PASSWORD_RESET_TOKEN_KEY_PATTERN, email);
    redisService.delete(key);
  }

  public boolean isOtpExists(String email) {
    String key = String.format(PASSWORD_RESET_OTP_KEY_PATTERN, email);
    return redisService.exists(key);
  }
}
