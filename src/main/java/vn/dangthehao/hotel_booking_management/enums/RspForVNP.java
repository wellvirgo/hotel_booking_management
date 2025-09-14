package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RspForVNP {
  CONFIRM_SUCCESS("00", "Confirm Success"),
  PAYMENT_NOT_FOUND("01", "Payment not found"),
  ALREADY_CONFIRMED("02", "Payment already confirmed"),
  INVALID_AMOUNT("04", "Invalid amount"),
  INVALID_CHECKSUM("97", "Invalid checksum"),
  UNKNOWN_ERROR("99", "Unknown error");

  String rspCode;
  String message;

  RspForVNP(String rspCode, String message) {
    this.rspCode = rspCode;
    this.message = message;
  }
}
