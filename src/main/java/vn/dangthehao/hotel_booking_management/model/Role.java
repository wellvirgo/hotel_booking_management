package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role extends BaseEntity {
  @Column(unique = true, nullable = false)
  String roleName;

  @Column(columnDefinition = "TEXT")
  String description;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id"))
  Set<Permission> permissions;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return Objects.equals(roleName, role.roleName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleName);
  }
}
