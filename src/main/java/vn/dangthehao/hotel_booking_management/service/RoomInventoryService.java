package vn.dangthehao.hotel_booking_management.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.model.RoomInventory;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.RoomInventoryRepository;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomInventoryService {
  static int INVENTORY_DURATION_MONTHS = 6;
  RoomInventoryRepository roomInventoryRepo;

  public void createRoomInventories(RoomType roomType) {
    List<RoomInventory> roomInventories = generateRoomInventories(roomType);
    roomInventoryRepo.saveAll(roomInventories);
  }

  public void increaseRoomInventory(
      Long roomTypeId, LocalDate checkIn, LocalDate checkOut, int quantity) {
    int result = roomInventoryRepo.increaseAvailableRooms(roomTypeId, checkIn, checkOut, quantity);
    if (result == 0) {
      log.info("No room-inventory records were increased");
      return;
    }

    log.info("Room-inventory records were increased");
  }

  private List<RoomInventory> generateRoomInventories(RoomType roomType) {
    List<RoomInventory> roomInventories = new ArrayList<>();
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusMonths(INVENTORY_DURATION_MONTHS);
    for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
      RoomInventory roomInventory =
          RoomInventory.builder()
              .roomType(roomType)
              .inventoryDate(date)
              .totalRooms(roomType.getTotalRooms())
              .availableRooms(roomType.getTotalRooms())
              .build();

      roomInventories.add(roomInventory);
    }

    return roomInventories;
  }
}
