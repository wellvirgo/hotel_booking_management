package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Review extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @ManyToOne
  @JoinColumn(name = "hotel_id", nullable = false)
  Hotel hotel;

  @Column(nullable = false, columnDefinition = "int default 0")
  int rating = 0;

  @Column(columnDefinition = "TEXT")
  String comment;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    Review review = (Review) object;
    return Objects.equals(getId(), review.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getClass().hashCode());
  }
}
