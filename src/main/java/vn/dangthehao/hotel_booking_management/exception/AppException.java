package vn.dangthehao.hotel_booking_management.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public AppException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public AppException(ErrorCode errorCode, Object... args) {
        this(errorCode, String.format(errorCode.getMessage(), args));
    }

    public AppException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode=errorCode;
        this.errorMessage=errorMessage;
    }
}
