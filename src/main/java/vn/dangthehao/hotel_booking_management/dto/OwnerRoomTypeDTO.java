package vn.dangthehao.hotel_booking_management.dto;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerRoomTypeDTO {
  String name;
  String description;
  BigDecimal pricePerNight;
  boolean active;
}
