package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.PaymentMethod;
import vn.dangthehao.hotel_booking_management.enums.PaymentStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Payment extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false)
  Booking booking;

  @Column(nullable = false, precision = 10, scale = 2)
  BigDecimal amount;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  PaymentMethod paymentMethod;

  @Column(nullable = false, unique = true)
  String transactionId;

  @Enumerated(value = EnumType.STRING)
  PaymentStatus status = PaymentStatus.DEPOSIT_PENDING;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Payment payment = (Payment) object;
    return Objects.equals(transactionId, payment.transactionId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(transactionId);
  }
}
