package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserCrtResponse;
import vn.dangthehao.hotel_booking_management.service.AuthenticationService;
import vn.dangthehao.hotel_booking_management.service.UserService;

import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserCrtResponse>> register(
            @Valid @RequestBody UserCrtRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorizationHeader) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.logout(authorizationHeader));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("refresh_token") String refreshToken
    ) {
        if (!"refresh_token".equals(grantType))
            throw new RuntimeException("invalid grant type");
        ApiResponse<AuthResponse> apiResponse = authenticationService.renewAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
