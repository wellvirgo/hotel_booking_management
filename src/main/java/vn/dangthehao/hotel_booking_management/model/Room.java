package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Room extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    Hotel hotel;

    String description;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal pricePerNight;

    @Column(nullable = false)
    int capacity;

    @Column(columnDefinition = "boolean default false", nullable = false)
    boolean isDeleted;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    RoomStatus status = RoomStatus.AVAILABLE;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity && Objects.equals(roomType, room.roomType)
                && Objects.equals(hotel, room.hotel)
                && Objects.equals(description, room.description)
                && Objects.equals(pricePerNight, room.pricePerNight) && status == room.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomType, hotel, description, pricePerNight, capacity, status);
    }
}
