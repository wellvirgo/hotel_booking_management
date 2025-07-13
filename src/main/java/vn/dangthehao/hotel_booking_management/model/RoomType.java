package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
public class RoomType extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    Hotel hotel;

    @Column(nullable = false, unique = true)
    String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @Column(precision = 10, scale = 2)
    BigDecimal pricePerNight;

    int capacity;
    int numOfBeds;
    String bedType;
    int totalRooms;

    @Column(columnDefinition = "boolean default true", nullable = false)
    boolean isActive;

    @ElementCollection
    @CollectionTable(name = "room_type_img",
            joinColumns = @JoinColumn(name = "room_type_id"))
    List<String> imageNames;

    @ManyToMany
    @JoinTable(
            name = "room_type_amenity",
            joinColumns = @JoinColumn(name = "room_type_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    Set<Amenity> amenities;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        RoomType roomType = (RoomType) object;
        return capacity == roomType.capacity
                && numOfBeds == roomType.numOfBeds
                && totalRooms == roomType.totalRooms
                && isActive == roomType.isActive
                && Objects.equals(hotel, roomType.hotel)
                && Objects.equals(name, roomType.name)
                && Objects.equals(description, roomType.description)
                && Objects.equals(pricePerNight, roomType.pricePerNight)
                && Objects.equals(bedType, roomType.bedType)
                && Objects.equals(amenities, roomType.amenities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotel, name, description, pricePerNight, capacity, numOfBeds, bedType, totalRooms, isActive, amenities);
    }
}
