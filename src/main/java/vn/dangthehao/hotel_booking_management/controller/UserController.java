package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.request.ChangePasswordRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.service.AuthService;
import vn.dangthehao.hotel_booking_management.service.UserService;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  UserService userService;
  JwtService jwtService;
  AuthService authService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
      @AuthenticationPrincipal Jwt jwt) {
    Long userId = jwtService.getUserId(jwt);

    String message = "Current user's information";
    UserResponse data = userService.getCurrentUser(userId);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(message, data));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UserCrtResponse>> register(
      @Valid @RequestBody UserCrtRequest request) {
    UserCrtResponse response = userService.register(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseBuilder.success("User is created", response));
  }

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserUpdateResponse>> updateProfile(
      @AuthenticationPrincipal Jwt jwt,
      @RequestPart(name = "data") UserUpdateRequest request,
      @RequestPart(name = "file") MultipartFile avatar) {
    Long userId = jwtService.getUserId(jwt);

    String message = "User is updated";
    UserUpdateResponse data = userService.updateUserInf(userId, request, avatar);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(message, data));
  }

  @PatchMapping("/me/password")
  public ResponseEntity<ApiResponse<AuthResponse>> changePassword(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
    Long userId = jwtService.getUserId(jwt);

    String message = "Password changed successfully!";
    AuthResponse data = authService.changePassword(request, userId);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(message, data));
  }

  @DeleteMapping("/me")
  @PreAuthorize("@permissionChecker.hasAuthorities({'user:delete'})")
  public ResponseEntity<ApiResponse<Void>> deleteMyAccount(@AuthenticationPrincipal Jwt jwt) {
    Long userId = jwtService.getUserId(jwt);

    userService.deleteMyAccount(userId);
    String message = "Your account is deleted";

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(message));
  }
}
