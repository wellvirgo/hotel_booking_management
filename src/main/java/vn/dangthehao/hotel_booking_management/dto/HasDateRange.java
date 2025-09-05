package vn.dangthehao.hotel_booking_management.dto;

import java.time.LocalDate;

public interface HasDateRange {
  LocalDate getCheckIn();

  LocalDate getCheckOut();
}
