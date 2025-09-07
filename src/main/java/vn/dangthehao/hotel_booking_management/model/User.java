package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
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
public class User extends BaseEntity {
  @Column(unique = true, nullable = false)
  String username;

  @Column(nullable = false)
  String password;

  @Column(nullable = false)
  String fullName;

  @Column(unique = true, nullable = false)
  String email;

  String phone;

  @Column(columnDefinition = "boolean default false", nullable = false)
  boolean isDeleted;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "role_id", nullable = false)
  Role role;

  String avatar;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    User user = (User) object;
    return Objects.equals(username, user.username) && Objects.equals(email, user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email);
  }
}
