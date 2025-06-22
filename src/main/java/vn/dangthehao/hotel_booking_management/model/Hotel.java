package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    String description;
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

    @ManyToMany
    @JoinTable(name = "hotels_room_types",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "room_type_id"))
    Set<RoomType> roomTypes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Float.compare(rating, hotel.rating) == 0
                && Objects.equals(hotelName, hotel.hotelName)
                && Objects.equals(owner, hotel.owner)
                && Objects.equals(address, hotel.address)
                && Objects.equals(description, hotel.description)
                && status == hotel.status && Objects.equals(roomTypes, hotel.roomTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelName, owner, address, description, rating, status, roomTypes);
    }
}
