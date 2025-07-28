package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class RefreshToken {
  @Id String id;

  @Column(nullable = false, length = 512)
  String token;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(nullable = false)
  LocalDateTime expiredTime;
}
