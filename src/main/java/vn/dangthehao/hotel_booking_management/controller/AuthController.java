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
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.request.CheckEmailRequest;
import vn.dangthehao.hotel_booking_management.dto.request.ResetPasswordRequest;
import vn.dangthehao.hotel_booking_management.dto.request.VerifyOTPRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.dto.response.VerifyOTPResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.service.AuthenticationService;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    AuthenticationService authenticationService;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(request));
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.logout(jwt));
    }

    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken) {
        if (!"refresh_token".equals(grantType))
            throw new AppException(ErrorCode.INVALID_GRANT_TYPE_TOKEN);
        ApiResponse<AuthResponse> apiResponse = authenticationService.renewAccessAndRefreshToken(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/passwords/resets")
    public ResponseEntity<ApiResponse<Void>> sendOTP(
            @RequestBody CheckEmailRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.sendOTP(request));
    }

    @PostMapping("/passwords/resets/verify")
    public ResponseEntity<ApiResponse<VerifyOTPResponse>> verifyOTP(@RequestBody VerifyOTPRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.verifyOTP(request));
    }

    @PutMapping("/passwords/resets")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            @RequestHeader("C-Reset-Token") String resetToken) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resetPassword(request, resetToken));
    }
}
