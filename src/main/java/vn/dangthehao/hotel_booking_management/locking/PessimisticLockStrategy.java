package vn.dangthehao.hotel_booking_management.locking;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.model.RoomInventory;
import vn.dangthehao.hotel_booking_management.repository.RoomInventoryRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("pessimistic")
public class PessimisticLockStrategy implements BookingLockStrategy {
  RoomInventoryRepository roomInventoryRepository;

  @Override
  public List<RoomInventory> lock(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
    return roomInventoryRepository.lockByRoomTypeIdAndDateRange(roomTypeId, checkIn, checkOut);
  }

  @Override
  public void unlock(Long roomTypeId) {}
}
