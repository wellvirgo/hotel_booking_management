package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelBaseInfoDTO {
  Long id;
  String hotelName;
  String address;
  String location;
  String thumbnail;
  float rating;
}
