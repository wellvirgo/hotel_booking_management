package vn.dangthehao.hotel_booking_management.util;

import org.springframework.http.HttpStatus;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;

public class SuccessResponse {
    public static <T> ApiResponse<T> buildSuccessResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> buildSuccessResponse(String message) {
        return ApiResponse.<T>builder()
                .status("Success")
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }
}
