package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

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
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    RoomType roomType;

    int numRooms;

    @Column(nullable = false)
    LocalDateTime checkIn;

    @Column(nullable = false)
    LocalDateTime checkOut;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    BigDecimal depositAmount;
    LocalDateTime depositDeadline;

    @Enumerated(value = EnumType.STRING)
    BookingStatus status= BookingStatus.PENDING;

    @OneToMany(mappedBy = "booking")
    Set<BookingRoom> bookingRooms;
}
