package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;
import vn.dangthehao.hotel_booking_management.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @NonNull
  <S extends Booking> S save(@NonNull S booking);

  @Query("select b from Booking b where b.id=:id")
  @EntityGraph(attributePaths = {"bookingRooms", "bookingRooms.room"})
  Optional<Booking> findByIdFetchRooms(Long id);

  @Query("select b.roomType.id from Booking b where b.id=:bookingId")
  Optional<Long> findRoomTypeIdByBookingId(Long bookingId);

  @Modifying
  @Query("update Booking b set b.status=:newStatus, b.updatedAt=current_timestamp where b.id=:id")
  int updateBookingStatusById(Long id, BookingStatus newStatus);
}
