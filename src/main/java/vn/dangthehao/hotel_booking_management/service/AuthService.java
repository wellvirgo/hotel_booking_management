package vn.dangthehao.hotel_booking_management.service;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.dto.response.PasswordResetOtpVerifyResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.security.JwtProvider;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.security.SecurityUtils;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;
import vn.dangthehao.hotel_booking_management.util.OTPUtils;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {
  UserService userService;
  MailService mailService;
  JwtService jwtService;
  RefreshTokenService refreshTokenService;
  TokenBlackListService tokenBlackListService;
  PasswordResetCacheService passwordResetCacheService;
  JwtProvider jwtProvider;
  PasswordEncoder passwordEncoder;

  public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
    User user = verifyCredentials(request);

    String accessToken = jwtProvider.generateToken(user);
    String refreshToken = refreshTokenService.issueRefreshToken(user.getId());

    AuthResponse data = ApiResponseBuilder.auth(accessToken, refreshToken);

    return ApiResponseBuilder.success("Is authenticated", data);
  }

  public ApiResponse<AuthResponse> renewTokenPair(String currRefreshToken) {
    Long userId = refreshTokenService.getUserId(currRefreshToken);

    if (!refreshTokenService.verifyRefreshToken(userId, currRefreshToken))
      throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);

    User user = userService.getByIdWithRole(userId);
    String newAccessToken = jwtProvider.generateToken(user);
    String newRefreshToken = refreshTokenService.renewRefreshToken(userId, currRefreshToken);

    AuthResponse data = ApiResponseBuilder.auth(newAccessToken, newRefreshToken);

    return ApiResponseBuilder.success("Access and refresh token are renewed", data);
  }

  public ApiResponse<String> logout(Jwt accessJwt, String refreshToken) {
    tokenBlackListService.revokeAccessToken(accessJwt);
    refreshTokenService.revoke(jwtService.getUserId(accessJwt), refreshToken);

    SecurityUtils.clearAuthentication();

    return ApiResponseBuilder.success("Logout successfully");
  }

  public ApiResponse<String> logoutAll(Jwt accessJwt) {
    Long userId = jwtService.getUserId(accessJwt);
    User user = userService.getById(userId);
    userService.updateTokenVersion(user);

    refreshTokenService.revokeAllByUser(userId);

    return ApiResponseBuilder.success("Logout all device successfully");
  }

  public AuthResponse changePassword(ChangePasswordRequest request, Long userId) {
    User user = userService.getByIdWithRole(userId);

    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
      throw new AppException(ErrorCode.WRONG_PASSWORD);

    userService.updatePassword(user, request.getPassword());

    // Force logout
    userService.updateTokenVersion(user);
    refreshTokenService.revokeAllByUser(userId);

    // Renew a token pair
    mailService.sendPasswordChangedAsync(user.getEmail());

    return AuthResponse.builder()
        .accessToken(jwtProvider.generateToken(user))
        .refreshToken(refreshTokenService.issueRefreshToken(userId))
        .build();
  }

  public ApiResponse<Void> sendPasswordResetOtp(PasswordResetOtpRequest request) {
    if (!userService.isEmailExists(request.getEmail()))
      throw new AppException(ErrorCode.EMAIL_NOT_EXIST);

    String otp = OTPUtils.generateOTP();
    String email = request.getEmail();

    mailService.sendOtpAsync(email, otp);
    passwordResetCacheService.cacheOtp(email, otp);

    return ApiResponseBuilder.success("OTP will be sent to your email");
  }

  public ApiResponse<PasswordResetOtpVerifyResponse> verifyPasswordResetOtp(
      PasswordResetOtpVerifyRequest request) {
    String email = request.getEmail();

    if (!request.getOtp().equals(passwordResetCacheService.getOtp(email))) {
      throw new AppException(ErrorCode.INVALID_OTP);
    }

    // Delete otp from redis
    passwordResetCacheService.deleteOtp(email);

    // Store reset token into redis
    String resetToken = String.valueOf(UUID.randomUUID());
    passwordResetCacheService.cacheToken(email, resetToken);

    PasswordResetOtpVerifyResponse data =
        PasswordResetOtpVerifyResponse.builder().resetToken(resetToken).build();

    return ApiResponseBuilder.success("Verify OTP successfully", data);
  }

  public ApiResponse<Void> resetPassword(ResetPasswordRequest request, String resetToken) {
    String email = request.getEmail();

    // Check reset token
    resetToken = resetToken == null ? "" : resetToken;
    if (!resetToken.equals(passwordResetCacheService.getToken(email)))
      throw new AppException(ErrorCode.INVALID_RESET_TOKEN);

    User user = userService.getByEmail(email);
    userService.updatePassword(user, request.getPassword());

    // Force logout of account
    userService.updateTokenVersion(user);
    refreshTokenService.revokeAllByUser(user.getId());

    passwordResetCacheService.deleteToken(email);
    mailService.sendPasswordResetAsync(email);

    return ApiResponseBuilder.success("Reset password successfully");
  }

  private User verifyCredentials(AuthRequest request) {
    User user = userService.getByUsernameWithRole(request.getUsername());
    boolean isPassValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
    if (!isPassValid) throw new AppException(ErrorCode.WRONG_PASSWORD);

    return user;
  }
}
