package vn.dangthehao.hotel_booking_management.util;

import org.springframework.http.HttpStatus;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AuthResponse;

public class ApiResponseBuilder {

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .status("Success")
        .code(HttpStatus.OK.value())
        .message(message)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> success(String message) {
    return ApiResponse.<T>builder()
        .status("Success")
        .code(HttpStatus.OK.value())
        .message(message)
        .build();
  }

  public static AuthResponse auth(String accessToken, String refreshToken) {
    return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }
}
