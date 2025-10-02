package vn.dangthehao.hotel_booking_management.enums;

public enum HotelStatus {
  PENDING,
  ACTIVE,
  INACTIVE,
  REJECTED,
  SUSPENDED;

  public static boolean canConfigure(HotelStatus status) {
    return status != PENDING && status != REJECTED && status != SUSPENDED;
  }
}
