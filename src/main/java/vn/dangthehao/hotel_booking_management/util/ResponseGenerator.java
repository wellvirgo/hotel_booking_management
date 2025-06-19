package vn.dangthehao.hotel_booking_management.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;
import vn.dangthehao.hotel_booking_management.security.TokenGenerator;

import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class ResponseGenerator {
    TokenGenerator tokenGenerator;

    public <T> ApiResponse<T> generateSuccessResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public <T> ApiResponse<T> generateSuccessResponse(String message) {
        return ApiResponse.<T>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    public AuthResponse generateAuthResponse(Map<String, String> keyPair) {
        return AuthResponse.builder()
                .accessToken(keyPair.get("accessToken"))
                .refreshToken(keyPair.get("refreshToken"))
                .build();
    }
}
