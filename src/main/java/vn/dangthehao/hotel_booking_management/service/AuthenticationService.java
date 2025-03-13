package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
    UserRepository userRepository;
    TokenGenerator tokenGenerator;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonFinal
    @Value("${jwt.private_key}")
    String privateKey;

    @NonFinal
    @Value("${jwt.valid_duration}")
    long validDuration;

    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean checkPassword = bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword());
        if (!checkPassword)
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        String token = tokenGenerator.generateToken(user);

        return ApiResponse.<AuthResponse>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message("is authenticated")
                .data(AuthResponse.builder().token(token).build())
                .build();
    }
}
