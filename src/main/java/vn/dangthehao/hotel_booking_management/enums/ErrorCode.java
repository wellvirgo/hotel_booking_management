package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, ErrorMessage.M_USER_NOT_FOUND.getMessage()),
    WRONG_PASSWORD(1002, HttpStatus.BAD_REQUEST, ErrorMessage.M_WRONG_PASSWORD.getMessage()),
    USERNAME_IS_EXISTED(1003, HttpStatus.CONFLICT, ErrorMessage.M_USERNAME_IS_EXISTED.getMessage()),
    UNAUTHENTICATED(1004, HttpStatus.UNAUTHORIZED, ErrorMessage.M_UNAUTHENTICATED.getMessage()),
    UNAUTHORIZED(1005, HttpStatus.FORBIDDEN, ErrorMessage.M_UNAUTHORIZED.getMessage()),
    ROLE_NOT_FOUND(1006, HttpStatus.BAD_REQUEST, ErrorMessage.M_ROLE_NOT_FOUND.getMessage()),
    FAILED_UPLOAD_FILE(1007, HttpStatus.BAD_REQUEST, ErrorMessage.M_FAILED_UPLOAD_FILE.getMessage()),
    VALIDATION_FAILED(1008, HttpStatus.BAD_REQUEST, ErrorMessage.M_VALIDATION_FAILED.getMessage()),
    EMAIL_IS_EXISTED(1009, HttpStatus.CONFLICT, ErrorMessage.M_EMAIL_IS_EXISTED.getMessage()),
    UNCATEGORIZED_EXCEPTION(9999, HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.M_UNCATEGORIZED.getMessage());

    int code;
    HttpStatus httpStatus;
    String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
