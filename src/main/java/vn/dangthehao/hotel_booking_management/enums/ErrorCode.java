package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "User not found"),
    WRONG_PASSWORD(1002, HttpStatus.BAD_REQUEST, "Password is incorrect"),
    UNAUTHENTICATED(1003, HttpStatus.UNAUTHORIZED, "Unauthenticated"),
    UNAUTHORIZED(1004, HttpStatus.FORBIDDEN, "Access is denied"),
    ROLE_NOT_FOUND(1005, HttpStatus.BAD_REQUEST, "Role not found"),
    FAILED_UPLOAD_FILE(1006, HttpStatus.BAD_REQUEST, "Failed upload file"),
    VALIDATION_FAILED(1007, HttpStatus.BAD_REQUEST, "Validation failed"),
    TOKEN_IS_REVOKED(1008, HttpStatus.UNAUTHORIZED, "Token is revoked"),
    REFRESH_TOKEN_NOT_FOUND(1009, HttpStatus.NOT_FOUND, "Refresh token not found"),
    WEAK_PASSWORD(1010, HttpStatus.BAD_REQUEST,
            "Password must contain at least 8 characters," +
                    "including an uppercase letter, a lowercase letter, a number, and a special character."),
    THE_SAME_OLD_PASSWORD(1011, HttpStatus.BAD_REQUEST, "New password must be different old password"),
    UNCATEGORIZED_EXCEPTION(9999, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    int code;
    HttpStatus httpStatus;
    String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
