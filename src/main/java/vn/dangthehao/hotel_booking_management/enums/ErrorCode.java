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
  WEAK_PASSWORD(
      1010,
      HttpStatus.BAD_REQUEST,
      "Password must contain at least 8 characters,"
          + "including an uppercase letter, a lowercase letter, a number, and a special character."),
  THE_SAME_OLD_PASSWORD(
      1011, HttpStatus.BAD_REQUEST, "New password must be different old password"),
  INVALID_GRANT_TYPE_TOKEN(1012, HttpStatus.BAD_REQUEST, "Invalid grant type refresh"),
  FAILED_PARSE_TOKEN(1013, HttpStatus.BAD_REQUEST, "Failed to parse jwt token"),
  INVALID_REFRESH_TOKEN(1014, HttpStatus.BAD_REQUEST, "Refresh token is invalid"),
  EMAIL_NOT_EXIST(1015, HttpStatus.BAD_REQUEST, "Email is not registered in the system"),
  INVALID_OTP(1016, HttpStatus.BAD_REQUEST, "Invalid OTP"),
  INVALID_RESET_TOKEN(1017, HttpStatus.BAD_REQUEST, "Reset password token is invalid"),
  HOTEL_NOT_FOUND(1018, HttpStatus.NOT_FOUND, "Hotel not found"),
  CAN_NOT_APPROVE_HOTEL(1019, HttpStatus.BAD_REQUEST, "Can't approve the hotel"),
  CAN_NOT_REJECT_HOTEL(1020, HttpStatus.BAD_REQUEST, "Can't reject the hotel"),
  AMENITY_EXISTS(1021, HttpStatus.BAD_REQUEST, "Amenity already exists"),
  DUPLICATE_DATA(1022, HttpStatus.BAD_REQUEST, "Invalid data, duplicate data in field not allowed"),
  HOTEL_NOT_APPROVED(1023, HttpStatus.BAD_REQUEST, "Hotel is not approved"),
  ROOM_TYPE_NOT_FOUND(1024, HttpStatus.NOT_FOUND, "Room type with id %s not found"),
  QUANTITY_ROOM_EXCEEDED(1025, HttpStatus.BAD_REQUEST, "The number of rooms has reached maximum"),
  ILLEGAL_LOCK_TYPE(1026, HttpStatus.BAD_REQUEST, "Lock type %s is not supported"),
  NOT_ENOUGH_ROOMS(1027, HttpStatus.BAD_REQUEST, "Not enough rooms in room type %s"),
  UNSUPPORTED_HASH_ALGORITHM(
      1028, HttpStatus.INTERNAL_SERVER_ERROR, "Hash algorithm %s is not supported"),
  PAYMENT_NOT_FOUND(1029, HttpStatus.NOT_FOUND, "Payment with %s not found"),
  INVALID_STATUS_TRANSITION(
      1030, HttpStatus.INTERNAL_SERVER_ERROR, "%s cannot jump from %s status to %s status"),
  BOOKING_NOT_FOUND(1031, HttpStatus.NOT_FOUND, "Booking with id %s not found"),
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
