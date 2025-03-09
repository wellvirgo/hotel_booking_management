package vn.dangthehao.hotel_booking_management.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.AuthRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.model.User;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;

import java.text.ParseException;
import java.util.Date;

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
                .orElseThrow(() -> new RuntimeException("user not found"));
        boolean checkPassword = bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword());
        if (!checkPassword) throw new RuntimeException("password is incorrect");
        String key = tokenGenerator.generateToken(user);

        return ApiResponse
                .success(200, "is authenticated",
                        AuthResponse.builder().token(key).build());
    }
}
