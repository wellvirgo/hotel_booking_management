package vn.dangthehao.hotel_booking_management.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.dto.response.VerifyOTPResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.RefreshToken;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.security.CustomDecoder;
import vn.dangthehao.hotel_booking_management.security.SecurityUtils;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
import vn.dangthehao.hotel_booking_management.util.OTPUtil;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;
import vn.dangthehao.hotel_booking_management.util.TimeConverter;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
  UserService userService;
  TokenGenerator tokenGenerator;
  BCryptPasswordEncoder bCryptPasswordEncoder;
  CustomDecoder customDecoder;
  RefreshTokenService refreshTokenService;
  TokenBlackListService tokenBlackListService;
  MailService mailService;
  ResponseGenerator responseGenerator;
  JwtUtil jwtUtil;
  RedisService redisService;

  public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
    User user = checkUsernameAndPassword(request);
    Map<String, String> keyPair = tokenGenerator.generateTokenPair(user);
    refreshTokenService.updateRefreshToken(user, keyPair.get("refreshToken"));
    AuthResponse data = responseGenerator.generateAuthResponse(keyPair);

    return responseGenerator.generateSuccessResponse("Is authenticated", data);
  }

  public ApiResponse<AuthResponse> renewAccessAndRefreshToken(String refreshToken) {
    if (!refreshTokenService.verifyRefreshToken(refreshToken))
      throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    if (isExpired(refreshToken)) throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);

    User user = userService.findByID(jwtUtil.getUserID(jwtUtil.getClaims(refreshToken)));
    Map<String, String> keyPair = tokenGenerator.generateTokenPair(user);
    refreshTokenService.updateRefreshToken(user, keyPair.get("refreshToken"));
    AuthResponse data = responseGenerator.generateAuthResponse(keyPair);

    return responseGenerator.generateSuccessResponse("Access and refresh token are renewed", data);
  }

  public ApiResponse<String> logout(Jwt jwt) {
    LocalDateTime expiredTime = jwtUtil.getExpiredTime(jwt);
    LocalDateTime now = TimeConverter.instantToLocalDateTime(Instant.now());
    if (expiredTime.isAfter(now)) {
      tokenBlackListService.revokeAccessToken(jwt.getTokenValue());
    }

    Long userID = jwtUtil.getUserID(jwt);
    User user = userService.findByID(userID);
    RefreshToken refreshToken = refreshTokenService.findByUser(user);
    refreshTokenService.delete(refreshToken);
    SecurityUtils.clearAuthentication();

    return responseGenerator.generateSuccessResponse("Log out successfully");
  }

  public ApiResponse<AuthResponse> changePassword(ChangePasswordRequest request, Jwt jwt) {
    User user = validatePasswordAndGetUser(request, jwt);
    updatePassword(user, request.getPassword());
    logout(jwt);

    Map<String, String> keyPair = tokenGenerator.generateTokenPair(user);
    refreshTokenService.updateRefreshToken(user, keyPair.get("refreshToken"));
    AuthResponse data = responseGenerator.generateAuthResponse(keyPair);
    mailService.sendChangePasswordEmailAsync(user.getEmail());

    return responseGenerator.generateSuccessResponse("Change password successfully!", data);
  }

  public ApiResponse<Void> sendOTP(CheckEmailRequest request) {
    if (checkEmailExist(request)) {
      String otp = OTPUtil.generateOTP();
      String email = request.getEmail();
      mailService.sendOTPEmailAsync(email, otp);
      redisService.saveOTPWithExpiredTime(email, otp);
    }

    return responseGenerator.generateSuccessResponse("OTP will be sent to your email");
  }

  public ApiResponse<Void> resetPassword(ResetPasswordRequest request, String resetToken) {
    String email = request.getEmail();
    String keyResetToken = email + "_resetToken";
    // Check reset token
    resetToken = resetToken == null ? "" : resetToken;
    if (!resetToken.equals(redisService.get(keyResetToken)))
      throw new AppException(ErrorCode.INVALID_RESET_TOKEN);
    // If reset token is valid, allow reset password
    User user = userService.findByEmail(email);
    updatePassword(user, request.getPassword());
    mailService.sendChangePasswordEmailAsync(email);
    redisService.delete(keyResetToken);

    return responseGenerator.generateSuccessResponse("Reset password successfully");
  }

  public ApiResponse<VerifyOTPResponse> verifyOTP(VerifyOTPRequest request) {
    String email = request.getEmail();
    String keyOTP = email + "_otp";
    if (!request.getOtp().equals(redisService.get(keyOTP))) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }
    // Delete otp from redis
    redisService.delete(keyOTP);
    // Save reset token into redis
    String resetToken = String.valueOf(UUID.randomUUID());
    redisService.saveResetToken(email, resetToken);
    // Build response
    VerifyOTPResponse data = VerifyOTPResponse.builder().resetToken(resetToken).build();

    return responseGenerator.generateSuccessResponse("Verify OTP successfully", data);
  }

  private User checkUsernameAndPassword(AuthRequest request) {
    User user = userService.findByUsername(request.getUsername());
    boolean checkPassword =
        bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword());
    if (!checkPassword) throw new AppException(ErrorCode.WRONG_PASSWORD);

    return user;
  }

  public boolean checkEmailExist(CheckEmailRequest request) {
    String email = request.getEmail();
    if (!userService.checkEmailExist(email)) {
      throw new AppException(ErrorCode.EMAIL_NOT_EXIST);
    }

    return true;
  }

  public boolean isExpired(String token) {
    Jwt jwtToken = customDecoder.decode(token);
    LocalDateTime expiredTime =
        TimeConverter.instantToLocalDateTime(Objects.requireNonNull(jwtToken.getExpiresAt()));
    return expiredTime.isBefore(TimeConverter.instantToLocalDateTime(Instant.now()));
  }

  public Jwt getJwt() {
    return (Jwt) SecurityUtils.getAuthentication().getPrincipal();
  }

  private User validatePasswordAndGetUser(ChangePasswordRequest request, Jwt jwt) {
    Long userID = jwt.getClaim("userID");
    User user = userService.findByID(userID);
    String newPassword = request.getPassword();

    if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new AppException(ErrorCode.WRONG_PASSWORD);
    }
    if (request.getOldPassword().equals(newPassword)) {
      throw new AppException(ErrorCode.THE_SAME_OLD_PASSWORD);
    }

    return user;
  }

  private void updatePassword(User user, String newPassword) {
    user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    userService.saveOrUpdate(user);
  }
}
