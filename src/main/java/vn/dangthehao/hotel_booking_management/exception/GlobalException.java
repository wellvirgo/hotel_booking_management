package vn.dangthehao.hotel_booking_management.exception;

import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.ErrorDetail;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestControllerAdvice
public class GlobalException {
  String errorStatus = "Error";

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    ApiResponse<Void> response =
        ApiResponse.<Void>builder()
            .status(errorStatus)
            .code(exception.getErrorCode().getCode())
            .message(exception.getErrorMessage())
            .build();

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUncategorizedException() {
    ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
    ApiResponse<Void> response =
        ApiResponse.<Void>builder()
            .status(errorStatus)
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException exception) {
    ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
    List<ErrorDetail> errors = extractErrors(exception);
    ApiResponse<Void> response =
        ApiResponse.<Void>builder()
            .status(errorStatus)
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .errors(errors)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
      DataIntegrityViolationException exception) {
    String message = getConstraintViolationMessage(exception);
    ApiResponse<Void> response =
        ApiResponse.<Void>builder()
            .status(errorStatus)
            .code(ErrorCode.DUPLICATE_DATA.getCode())
            .message(message)
            .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException() {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    ApiResponse<Void> response =
        ApiResponse.<Void>builder()
            .status(errorStatus)
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();

    return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
  }

  private String getConstraintViolationMessage(DataIntegrityViolationException exception) {
    Throwable rootCause = exception.getRootCause();
    String message = rootCause != null ? rootCause.getMessage() : exception.getMessage();

    if (message != null && message.contains("room_type") && message.contains("Duplicate entry")) {
      return "Room type already exists. Please choose a different name";
    }

    return ErrorCode.DUPLICATE_DATA.getMessage();
  }

  private List<ErrorDetail> extractErrors(MethodArgumentNotValidException exception) {
    return exception.getBindingResult().getAllErrors().stream()
        .map(
            error -> {
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
