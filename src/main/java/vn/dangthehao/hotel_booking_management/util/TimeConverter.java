package vn.dangthehao.hotel_booking_management.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeConverter {
  public static LocalDateTime instantToLocalDateTime(Instant instant) {
    return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}
