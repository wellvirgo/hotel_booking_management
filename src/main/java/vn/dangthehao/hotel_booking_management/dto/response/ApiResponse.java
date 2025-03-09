package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    String status;
    int code;
    String message;
    T data;
    List<ErrorDetail> errors;

    public static <T> ApiResponse<T> success(int code, String message, T data){
        return ApiResponse.<T>builder()
                .status("success")
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, List<ErrorDetail> errors){
        return ApiResponse.<T>builder()
                .status("error")
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}
