package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "room_inventory",
    uniqueConstraints = @UniqueConstraint(columnNames = {"room_type_id", "inventory_date"}))
public class RoomInventory extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "room_type_id")
  RoomType roomType;

  @Column(name = "inventory_date")
  LocalDate inventoryDate;

  @Column(nullable = false)
  int totalRooms;

  int availableRooms;
}
