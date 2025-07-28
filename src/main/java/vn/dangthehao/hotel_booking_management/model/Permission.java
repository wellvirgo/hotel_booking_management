package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Permission extends BaseEntity {
  @Column(unique = true, nullable = false)
  String permissionName;

  @Column(columnDefinition = "TEXT")
  String description;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Permission that = (Permission) o;
    return Objects.equals(permissionName, that.permissionName)
        && Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(permissionName, description);
  }
}
