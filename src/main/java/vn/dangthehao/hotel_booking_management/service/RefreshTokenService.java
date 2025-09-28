package vn.dangthehao.hotel_booking_management.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class RefreshTokenService {
  static final String REFRESH_TOKEN_KEY = "auth:token:refresh:%d:%s";
  static final String USER_KEY_PATTERN = "auth:token:refresh:%d:*";

  final StringRedisTemplate stringRedisTemplate;

  @Value("${refresh-token.ttl}")
  Long tokenTtl;

  public String generateRefreshToken(Long userId) {
    final var SECURE_RANDOM = new SecureRandom();
    final var base64Encoder = Base64.getUrlEncoder().withoutPadding();

    var randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    String rawToken = base64Encoder.encodeToString(randomBytes);

    String refreshToken = userId + ":" + rawToken;
    return base64Encoder.encodeToString(refreshToken.getBytes(StandardCharsets.UTF_8));
  }

  public Long getUserId(String refreshToken) {
    final var base64Decoder = Base64.getUrlDecoder();

    byte[] decodedBytes = base64Decoder.decode(refreshToken);
    String rawToken = new String(decodedBytes, StandardCharsets.UTF_8);

    if (!rawToken.contains(":")) throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

    try {
      return Long.parseLong(rawToken.split(":")[0]);
    } catch (NumberFormatException e) {
      throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
  }

  public String issueRefreshToken(Long userId) {
    String token = generateRefreshToken(userId);
    String key = tokenKey(userId, token);
    stringRedisTemplate.opsForValue().set(key, String.valueOf(userId), tokenTtl, TimeUnit.DAYS);

    return token;
  }

  public boolean verifyRefreshToken(Long userId, String token) {
    String key = tokenKey(userId, token);
    return stringRedisTemplate.hasKey(key);
  }

  public void revoke(Long userId, String refreshToken) {
    String key = tokenKey(userId, refreshToken);
    stringRedisTemplate.delete(key);
  }

  public void revokeAllByUser(Long userId) {
    String pattern = userKeyPattern(userId);

    stringRedisTemplate.execute(
        (RedisCallback<Void>)
            connection -> {
              ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
              RedisCommands commands = connection.commands();

              try (Cursor<byte[]> cursor = commands.scan(options)) {
                while (cursor.hasNext()) {
                  byte[] key = cursor.next();
                  connection.keyCommands().del(key);
                }
              }

              return null;
            });
  }

  public String renewRefreshToken(Long userId, String currToken) {
    String currKey = tokenKey(userId, currToken);

    return stringRedisTemplate.execute(
        (RedisCallback<String>)
            connection -> {
              connection.keyCommands().del(serializeString(currKey));

              String newToken = generateRefreshToken(userId);
              String newKey = tokenKey(userId, newToken);

              connection
                  .stringCommands()
                  .setEx(
                      serializeString(newKey),
                      Duration.ofDays(tokenTtl).getSeconds(),
                      serializeString(String.valueOf(userId)));

              return newToken;
            });
  }

  private String tokenKey(Long userId, String refreshToken) {
    return String.format(REFRESH_TOKEN_KEY, userId, refreshToken);
  }

  private String userKeyPattern(Long userId) {
    return String.format(USER_KEY_PATTERN, userId);
  }

  private byte[] serializeString(String data) {
    return stringRedisTemplate.getStringSerializer().serialize(data);
  }
}
