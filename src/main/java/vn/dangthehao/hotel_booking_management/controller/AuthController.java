package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.request.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.dto.response.PasswordResetOtpVerifyResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.service.AuthService;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.authenticate(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody LogoutRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(authService.logout(jwt, request.getRefreshToken()));
  }

  @PostMapping("/logout/all")
  public ResponseEntity<ApiResponse<String>> logoutAll(@AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.logoutAll(jwt));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AuthResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {
    if (!"refresh_token".equals(request.getGrantType()))
      throw new AppException(ErrorCode.INVALID_GRANT_TYPE_TOKEN);

    ApiResponse<AuthResponse> apiResponse = authService.renewTokenPair(request.getRefreshToken());

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @PostMapping("/passwords/resets")
  public ResponseEntity<ApiResponse<Void>> requestPasswordResetOtp(
      @Valid @RequestBody PasswordResetOtpRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.sendPasswordResetOtp(request));
  }

  @PostMapping("/passwords/resets/verify")
  public ResponseEntity<ApiResponse<PasswordResetOtpVerifyResponse>> verifyOTP(
      @Valid @RequestBody PasswordResetOtpVerifyRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.verifyPasswordResetOtp(request));
  }

  @PutMapping("/passwords/resets")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request,
      @RequestHeader(value = "C-Reset-Token") String resetToken) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(authService.resetPassword(request, resetToken));
  }
}
