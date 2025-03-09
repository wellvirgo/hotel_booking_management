package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.PaymentMethod;
import vn.dangthehao.hotel_booking_management.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Objects;

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
    String transaction_id;

    @Enumerated(value = EnumType.STRING)
    PaymentStatus status = PaymentStatus.DEPOSIT_PENDING;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(booking, payment.booking)
                && Objects.equals(amount, payment.amount)
                && paymentMethod == payment.paymentMethod
                && Objects.equals(transaction_id, payment.transaction_id)
                && status == payment.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(booking, amount, paymentMethod, transaction_id, status);
    }
}
