package vn.dangthehao.hotel_booking_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @NonNull
  <S extends Booking> S save(@NonNull S booking);
}
