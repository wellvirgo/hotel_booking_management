package vn.dangthehao.hotel_booking_management.model.entity;

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
public class RoomType extends BaseEntity{
    @Column(nullable = false)
    String room_type_name;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoomType roomType = (RoomType) o;
        return Objects.equals(room_type_name, roomType.room_type_name)
                && Objects.equals(description, roomType.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(room_type_name, description);
    }
}
