package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Amenity extends BaseEntity {
    @Column(nullable = false, unique = true)
    String name;

    @Column(columnDefinition = "boolean default true")
    boolean isActive = true;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Amenity amenity = (Amenity) object;
        return isActive == amenity.isActive && Objects.equals(name, amenity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isActive);
    }
}
