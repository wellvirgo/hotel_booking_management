package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnapprovedHotelDTO {
  String hotelName;
  String ownerName;
  String address;
  String location;
  Float depositRate;
  Float depositDeadlineHours;
}
