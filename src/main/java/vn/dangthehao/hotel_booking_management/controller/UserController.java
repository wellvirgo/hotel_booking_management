package vn.dangthehao.hotel_booking_management.controller;

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
import vn.dangthehao.hotel_booking_management.dto.request.UserUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserUpdateResponse;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.security.PermissionChecker;
import vn.dangthehao.hotel_booking_management.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api")
public class UserController {
    UserService userService;
    PermissionChecker permissionChecker;

    @GetMapping("/admin/users")
    @PreAuthorize("@permissionChecker.hasAuthorities({'read:user','all:user'})")
    public ResponseEntity<ApiResponse<List<UserListResponse>>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.listUsers());
    }

    @PutMapping("/users/update-profile")
    public ResponseEntity<ApiResponse<UserUpdateResponse>> updateUserProfile(
            @AuthenticationPrincipal Jwt jwtToken,
            @RequestPart(name = "data") UserUpdateRequest request,
            @RequestPart(name = "file") MultipartFile file
    ) {
        Long userId = jwtToken.getClaim("userID");
        User currentUser = userService.findByID(userId);
        ApiResponse<UserUpdateResponse> response = userService.updateAccountInf(currentUser, request, file);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
