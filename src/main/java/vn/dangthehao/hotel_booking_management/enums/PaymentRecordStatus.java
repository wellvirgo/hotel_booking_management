package vn.dangthehao.hotel_booking_management.enums;

public enum PaymentRecordStatus {
  PENDING,
  SUCCESS,
  FAILED,
  REFUNDED,
  CANCELLED;

  public boolean canTransitionTo(PaymentRecordStatus next) {
    return switch (this) {
      case PENDING -> next == SUCCESS || next == FAILED || next == CANCELLED;
      case SUCCESS -> next == REFUNDED;
      case FAILED, CANCELLED, REFUNDED -> false;
    };
  }
}
