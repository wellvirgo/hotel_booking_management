package vn.dangthehao.hotel_booking_management.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.ErrorDetail;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;

import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestControllerAdvice
public class GlobalException {
    String errorStatus = "Error";

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(errorStatus)
                .code(exception.getErrorCode().getCode())
                .message(exception.getErrorCode().getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUncategorizedException() {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(errorStatus)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        List<ErrorDetail> errors = extractErrors(exception);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(errorStatus)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private List<ErrorDetail> extractErrors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return ErrorDetail.builder()
                                .objectName(error.getObjectName())
                                .field(fieldError.getField())
                                .message(fieldError.getDefaultMessage())
                                .build();
                    }
                    return ErrorDetail.builder()
                            .objectName(error.getObjectName())
                            .message(error.getDefaultMessage())
                            .build();
                })
                .toList();
    }
}
