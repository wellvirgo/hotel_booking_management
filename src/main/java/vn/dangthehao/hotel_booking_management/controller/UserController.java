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
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.request.ChangePasswordRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.service.AuthService;
import vn.dangthehao.hotel_booking_management.service.UserService;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  UserService userService;
  AuthService authenticationService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
      @AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getCurrentUser(jwt));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UserCrtResponse>> register(
      @Valid @RequestBody UserCrtRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
  }

  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
      @AuthenticationPrincipal Jwt jwtToken,
      @RequestPart(name = "data") UserUpdateRequest request,
      @RequestPart(name = "file") MultipartFile file) {
    Long userId = jwtToken.getClaim("userID");
    ApiResponse<UserUpdateResponse> response = userService.updateAccountInf(userId, request, file);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/me")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal Jwt jwt) {
    Long userID = jwt.getClaim("userID");
    return ResponseEntity.status(HttpStatus.OK).body(userService.deleteByID(userID));
  }

  @PatchMapping("/me/password")
  public ResponseEntity<ApiResponse<AuthResponse>> changePassword(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(authenticationService.changePassword(request, jwt));
  }
}
