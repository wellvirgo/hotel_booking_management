package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.RefreshToken;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.security.CustomDecoder;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
import vn.dangthehao.hotel_booking_management.util.TimeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


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
    JwtUtil jwtUtil;

    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        User user = checkUsernameAndPassword(request);
        Map<String, String> keyPair = generateTokenPair(user);

        return buildAuthResponse(keyPair);
    }

    public ApiResponse<AuthResponse> renewAccessToken(String refreshToken) {
        if (!refreshTokenService.verifyRefreshToken(refreshToken))
            throw new RuntimeException("Refresh token is invalid");
        if (isExpired(refreshToken))
            throw new RuntimeException("Refresh token is expired");

        User user = userService.findByID(jwtUtil.getUserID(jwtUtil.getClaims(refreshToken)));
        Map<String, String> keyPair = generateTokenPair(user);

        return buildAuthResponse(keyPair);
    }

    public ApiResponse<String> logout(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        LocalDateTime expiredTime = jwtUtil.getExpiredTime(jwtUtil.getClaims(token));
        LocalDateTime now = TimeConverter.instantToLocalDateTime(Instant.now());
        Long userID = jwtUtil.getUserID(jwtUtil.getClaims(token));
        User user = userService.findByID(userID);
        if (expiredTime.isAfter(now)) {
            tokenBlackListService.revokeAccessToken(token);
        }
        RefreshToken refreshToken = refreshTokenService.findByUser(user);
        refreshTokenService.delete(refreshToken);
        SecurityContextHolder.clearContext();

        return ApiResponse.<String>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message("Log out successfully")
                .build();
    }

    private User checkUsernameAndPassword(AuthRequest request) {
        User user = userService.findByUsername(request.getUsername());
        boolean checkPassword = bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword());
        if (!checkPassword)
            throw new AppException(ErrorCode.WRONG_PASSWORD);

        return user;
    }

    public Map<String, String> generateTokenPair(User user) {
        String accessToken = tokenGenerator.generateAccessToken(user);
        String refreshToken = tokenGenerator.generateRefreshToken(user);
        refreshTokenService.updateRefreshToken(user, refreshToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

    public boolean isExpired(String token) {
        Jwt jwtToken = customDecoder.decode(token);
        LocalDateTime expiredTime = TimeConverter.instantToLocalDateTime(jwtToken.getExpiresAt());
        return expiredTime.isBefore(TimeConverter.instantToLocalDateTime(Instant.now()));
    }

    private ApiResponse<AuthResponse> buildAuthResponse(Map<String, String> keyPair) {
        return ApiResponse.<AuthResponse>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message("is authenticated")
                .data(AuthResponse.builder()
                        .accessToken(keyPair.get("accessToken"))
                        .refreshToken(keyPair.get("refreshToken"))
                        .build())
                .build();
    }
}
