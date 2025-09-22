package vn.dangthehao.hotel_booking_management.enums;

public enum BookingStatus {
  PENDING,
  CONFIRMED,
  CHECKED_IN,
  CHECKED_OUT,
  CANCELLED,
  EXPIRED;

  public boolean canTransitionTo(BookingStatus next) {
    return switch (this) {
      case PENDING -> next == CONFIRMED || next == CANCELLED || next == EXPIRED;
      case CONFIRMED -> next == CHECKED_IN || next == CANCELLED || next == EXPIRED;
      case CHECKED_IN -> next == CHECKED_OUT;
      case CHECKED_OUT, CANCELLED, EXPIRED -> false;
    };
  }
}
