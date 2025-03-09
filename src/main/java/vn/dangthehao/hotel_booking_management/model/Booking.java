package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Booking extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @Column(nullable = false)
    LocalDateTime check_in;

    @Column(nullable = false)
    LocalDateTime check_out;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    BigDecimal depositAmount;
    LocalDateTime depositDeadline;

    @Enumerated(value = EnumType.STRING)
    BookingStatus status= BookingStatus.PENDING;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(user, booking.user)
                && Objects.equals(room, booking.room)
                && Objects.equals(check_in, booking.check_in)
                && Objects.equals(check_out, booking.check_out)
                && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, room, check_in, check_out, status);
    }
}
