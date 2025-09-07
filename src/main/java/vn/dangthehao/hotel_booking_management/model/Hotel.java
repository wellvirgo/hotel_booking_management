package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Hotel extends BaseEntity {
  @Column(nullable = false)
  String hotelName;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  User owner;

  @Column(nullable = false)
  String address;

  @Column(nullable = false)
  String location;

  String description;
  String thumbnail;
  float rating = 0;

  @Column(nullable = false)
  @Enumerated(value = EnumType.STRING)
  HotelStatus status = HotelStatus.INACTIVE;

  @Column(columnDefinition = "boolean default false", nullable = false)
  boolean isApproved;

  @Column(columnDefinition = "boolean default false", nullable = false)
  boolean isDeleted;

  Float depositRate;
  Long depositDeadlineMinutes;

  @Column(nullable = false)
  LocalTime checkInTime;

  @Column(nullable = false)
  LocalTime checkOutTime;

  @OneToMany(mappedBy = "hotel")
  Set<RoomType> roomTypes;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Hotel hotel = (Hotel) object;
    return Objects.equals(getId(), hotel.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
