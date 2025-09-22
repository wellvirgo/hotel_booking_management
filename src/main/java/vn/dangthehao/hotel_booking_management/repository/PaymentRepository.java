package vn.dangthehao.hotel_booking_management.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dangthehao.hotel_booking_management.enums.PaymentRecordStatus;
import vn.dangthehao.hotel_booking_management.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByTransactionId(String transactionId);

  @Query("select p from Payment p where p.booking.id=:bookingId and p.status=:status")
  @EntityGraph(attributePaths = {})
  Optional<Payment> findByBookingIdAndStatus(Long bookingId, PaymentRecordStatus status);
}
