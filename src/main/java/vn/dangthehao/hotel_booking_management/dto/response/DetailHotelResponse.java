package vn.dangthehao.hotel_booking_management.dto.response;

import java.time.LocalTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailHotelResponse {
  String hotelName;
  String address;
  String location;
  String description;
  float rating;
  String status;
  float depositRate;
  long depositDeadlineMinutes;
  LocalTime checkInTime;
  LocalTime checkOutTime;
  String ownerFullName;
  String ownerEmail;
  String ownerPhone;
  String ownerAvatar;
}
