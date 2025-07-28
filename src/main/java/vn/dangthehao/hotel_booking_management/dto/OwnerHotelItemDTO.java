package vn.dangthehao.hotel_booking_management.dto;

import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerHotelItemDTO {
  Long id;
  String hotelName;
  String address;
  LocalDateTime createdAt;
  HotelStatus status;
  float rating;
}
