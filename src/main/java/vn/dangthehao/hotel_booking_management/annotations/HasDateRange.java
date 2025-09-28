package vn.dangthehao.hotel_booking_management.annotations;

import java.time.LocalDate;

public interface HasDateRange {
  LocalDate getCheckIn();

  LocalDate getCheckOut();
}
