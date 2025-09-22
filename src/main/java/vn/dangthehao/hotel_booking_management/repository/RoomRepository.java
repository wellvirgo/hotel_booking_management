package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;
import vn.dangthehao.hotel_booking_management.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
  long countByRoomTypeId(Long id);

  List<Room> findByRoomTypeIdAndStatusOrderByRoomNumberAsc(
      Long roomTypeId, RoomStatus status, Pageable pageable);

  @Modifying
  @Query(
      "update Room r set r.status=:newStatus, r.updatedAt=current_timestamp where r.id in :roomIds")
  void updateRoomStatusByIds(List<Long> roomIds, RoomStatus newStatus);
}
