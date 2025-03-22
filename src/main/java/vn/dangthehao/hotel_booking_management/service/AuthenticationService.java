package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.request.ChangePasswordRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.RefreshToken;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.security.CustomDecoder;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
import vn.dangthehao.hotel_booking_management.util.SuccessResponse;
import vn.dangthehao.hotel_booking_management.util.TimeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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
        AuthResponse data = AuthResponse.builder()
                .accessToken(keyPair.get("accessToken"))
                .refreshToken(keyPair.get("refreshToken"))
                .build();

        return SuccessResponse.buildSuccessResponse("is authenticated", data);
    }

    public ApiResponse<AuthResponse> renewAccessAndRefreshToken(String refreshToken) {
        if (!refreshTokenService.verifyRefreshToken(refreshToken))
            throw new RuntimeException("Refresh token is invalid");
        if (isExpired(refreshToken))
            throw new RuntimeException("Refresh token is expired");

        User user = userService.findByID(jwtUtil.getUserID(jwtUtil.getClaims(refreshToken)));
        Map<String, String> keyPair = generateTokenPair(user);
        AuthResponse data = AuthResponse.builder()
                .accessToken(keyPair.get("accessToken"))
                .refreshToken(keyPair.get("refreshToken"))
                .build();

        return SuccessResponse.buildSuccessResponse("Access and refresh token are renewed", data);
    }

    public ApiResponse<String> logout(Jwt jwt) {
        LocalDateTime expiredTime = jwtUtil.getExpiredTime(jwt);
        LocalDateTime now = TimeConverter.instantToLocalDateTime(Instant.now());
        if (expiredTime.isAfter(now)) {
            tokenBlackListService.revokeAccessToken(jwt.getTokenValue());
        }
        Long userID = jwtUtil.getUserID(jwt);
        User user = userService.findByID(userID);
        RefreshToken refreshToken = refreshTokenService.findByUser(user);
        refreshTokenService.delete(refreshToken);
        SecurityContextHolder.clearContext();

        return SuccessResponse.buildSuccessResponse("Log out successfully");
    }

    public ApiResponse<AuthResponse> changePassword(ChangePasswordRequest request, Jwt jwt) {
        Long userID = jwt.getClaim("userID");
        User user = userService.findByID(userID);
        String newPassword = request.getPassword();
        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        if (request.getOldPassword().equals(request.getPassword())) {
            throw new AppException(ErrorCode.THE_SAME_OLD_PASSWORD);
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userService.saveOrUpdate(user);
        logout(jwt);
        Map<String, String> keyPair = generateTokenPair(user);
        AuthResponse data = AuthResponse.builder()
                .accessToken(keyPair.get("accessToken"))
                .refreshToken(keyPair.get("refreshToken"))
                .build();

        return SuccessResponse.buildSuccessResponse("Change password successfully!", data);
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
        LocalDateTime expiredTime = TimeConverter
                .instantToLocalDateTime(Objects.requireNonNull(jwtToken.getExpiresAt()));
        return expiredTime.isBefore(TimeConverter.instantToLocalDateTime(Instant.now()));
    }
}
