package vn.dangthehao.hotel_booking_management.controller;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.service.UserService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
  UserService userService;

  @GetMapping
  @PreAuthorize("@permissionChecker.hasAuthorities({'read:user','all:user'})")
  public ResponseEntity<ApiResponse<List<UserListResponse>>> getAllUsers() {
    return ResponseEntity.status(HttpStatus.OK).body(userService.listUsers());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteUserByAdmin(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.deleteByID(id));
  }
}
