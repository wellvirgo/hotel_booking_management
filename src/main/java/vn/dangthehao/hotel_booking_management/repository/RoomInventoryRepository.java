package vn.dangthehao.hotel_booking_management.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.RoomInventory;

@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {
  @Query(
      """
              select ri from RoomInventory ri
              where ri.roomType.id in :roomTypeIds
              and ri.inventoryDate between :checkIn and :checkOut""")
  List<RoomInventory> findByRoomTypeIdsAndDateRange(
      List<Long> roomTypeIds, LocalDate checkIn, LocalDate checkOut);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
              select ri from RoomInventory ri
              where ri.roomType.id = :roomTypeId
              and ri.inventoryDate >= :checkIn
              and ri.inventoryDate < :checkOut
              order by ri.inventoryDate asc""")
  List<RoomInventory> lockByRoomTypeIdAndDateRange(
      Long roomTypeId, LocalDate checkIn, LocalDate checkOut);
}
