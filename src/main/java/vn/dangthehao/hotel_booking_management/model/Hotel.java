package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
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
  Float depositDeadlineHours;

  @OneToMany(mappedBy = "hotel")
  Set<RoomType> roomTypes;

  @Override
  public boolean equals(Object object) {
    if (object == null || getClass() != object.getClass()) return false;
    Hotel hotel = (Hotel) object;
    return this.getId().equals(hotel.getId())
        && Float.compare(rating, hotel.rating) == 0
        && isApproved == hotel.isApproved
        && isDeleted == hotel.isDeleted
        && Objects.equals(hotelName, hotel.hotelName)
        && Objects.equals(address, hotel.address)
        && Objects.equals(location, hotel.location)
        && Objects.equals(description, hotel.description)
        && Objects.equals(thumbnail, hotel.thumbnail)
        && status == hotel.status
        && Objects.equals(depositRate, hotel.depositRate)
        && Objects.equals(depositDeadlineHours, hotel.depositDeadlineHours);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.getId(),
        hotelName,
        address,
        location,
        description,
        thumbnail,
        rating,
        status,
        isApproved,
        isDeleted,
        depositRate,
        depositDeadlineHours);
  }
}
