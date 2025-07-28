package vn.dangthehao.hotel_booking_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class InvalidToken {
  @Id String id;

  @Column(unique = true, nullable = false, length = 512)
  String token;

  @Column(nullable = false)
  LocalDateTime expiredTime;
}
