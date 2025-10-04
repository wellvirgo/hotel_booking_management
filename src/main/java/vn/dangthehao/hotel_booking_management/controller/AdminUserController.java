package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.service.AdminUserManagementService;
import vn.dangthehao.hotel_booking_management.service.UserService;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
  UserService userService;
  AdminUserManagementService adminUserManagementService;
  JwtService jwtService;

  @GetMapping
  public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "5") int size) {
    ApiResponse<UserListResponse> response =
        ApiResponseBuilder.success(
            "List of users", adminUserManagementService.listUsers(page, size));

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@permissionChecker.hasAuthorities({'user:*','user:delete'})")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @PathVariable("id") Long userId, @AuthenticationPrincipal Jwt jwt) {
    Long adminId = jwtService.getUserId(jwt);
    adminUserManagementService.deleteUser(userId, adminId);

    String message = String.format("User %d is deleted", userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponseBuilder.success(message));
  }
}
