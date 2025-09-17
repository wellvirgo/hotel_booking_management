package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;

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

  @Column(unique = true, nullable = false)
  String roomNumber;

  String description;

  @Column(columnDefinition = "boolean default true", nullable = false)
  boolean active;

  @Enumerated(value = EnumType.STRING)
  @Column(nullable = false)
  RoomStatus status = RoomStatus.AVAILABLE;

  @OneToMany(mappedBy = "room")
  Set<BookingRoom> bookingRooms;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Room room = (Room) object;
    return Objects.equals(roomNumber, room.roomNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(roomNumber);
  }
}
