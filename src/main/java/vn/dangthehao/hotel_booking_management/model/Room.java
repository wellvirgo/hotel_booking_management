package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    String roomNumber;
    String description;

    @Column(columnDefinition = "boolean default true", nullable = false)
    boolean isActive;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    RoomStatus status = RoomStatus.AVAILABLE;

    String images;

    @OneToMany(mappedBy = "room")
    Set<BookingRoom> bookingRooms;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Room room = (Room) object;
        return isActive == room.isActive
                && Objects.equals(roomType, room.roomType)
                && Objects.equals(roomNumber, room.roomNumber)
                && Objects.equals(description, room.description)
                && status == room.status && Objects.equals(images, room.images)
                && Objects.equals(bookingRooms, room.bookingRooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomType, roomNumber, description, isActive, status, images, bookingRooms);
    }
}
