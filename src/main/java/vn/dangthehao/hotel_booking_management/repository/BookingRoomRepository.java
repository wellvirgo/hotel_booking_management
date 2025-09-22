package vn.dangthehao.hotel_booking_management.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.model.BookingRoom;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, Long> {
  @Modifying
  @Query("delete from BookingRoom br where br.id in :bookingRoomIds")
  void deleteByIds(List<Long> bookingRoomIds);
}
