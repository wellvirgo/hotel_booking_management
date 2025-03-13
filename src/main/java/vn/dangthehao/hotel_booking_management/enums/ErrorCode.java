package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    INVALID_USERNAME(1001, HttpStatus.BAD_REQUEST, ErrorMessage.M_INVALID_USERNAME.getMessage()),
    INVALID_PASSWORD(1002, HttpStatus.BAD_REQUEST, ErrorMessage.M_INVALID_PASSWORD.getMessage()),
    USER_NOT_FOUND(1003, HttpStatus.NOT_FOUND, ErrorMessage.M_USER_NOT_FOUND.getMessage()),
    WRONG_PASSWORD(1004, HttpStatus.BAD_REQUEST, ErrorMessage.M_WRONG_PASSWORD.getMessage()),
    USERNAME_IS_EXISTED(1005, HttpStatus.BAD_REQUEST, ErrorMessage.M_USERNAME_IS_EXISTED.getMessage()),
    UNAUTHENTICATED(1006, HttpStatus.UNAUTHORIZED, ErrorMessage.M_UNAUTHENTICATED.getMessage()),
    UNAUTHORIZED(1007, HttpStatus.FORBIDDEN, ErrorMessage.M_UNAUTHORIZED.getMessage()),
    ROLE_NOT_FOUND(1008, HttpStatus.BAD_REQUEST, ErrorMessage.M_ROLE_NOT_FOUND.getMessage()),
    INVALID_FULLNAME(1009, HttpStatus.BAD_REQUEST, ErrorMessage.M_INVALID_FULLNAME.getMessage()),
    INVALID_EMAIL(1010, HttpStatus.BAD_REQUEST, ErrorMessage.M_INVALID_EMAIL.getMessage()),
    INVALID_PHONE(1011, HttpStatus.BAD_REQUEST, ErrorMessage.M_INVALID_PHONE.getMessage()),
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
