package vn.dangthehao.hotel_booking_management.locking;

import java.time.LocalDate;
import java.util.List;
import vn.dangthehao.hotel_booking_management.model.RoomInventory;

public interface BookingLockStrategy {
  List<RoomInventory> lock(Long roomTypeId, LocalDate checkIn, LocalDate checkOut);

  void unlock(Long roomTypeId);
}
