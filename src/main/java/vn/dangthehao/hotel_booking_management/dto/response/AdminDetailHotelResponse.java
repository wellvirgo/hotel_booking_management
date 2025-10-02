package vn.dangthehao.hotel_booking_management.dto.response;

import java.time.LocalTime;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.HotelBaseInfoDTO;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminDetailHotelResponse extends HotelBaseInfoDTO {
  String description;
  HotelStatus status;
  float depositRate;
  long depositDeadlineMinutes;
  LocalTime checkInTime;
  LocalTime checkOutTime;
  String ownerFullName;
  String ownerEmail;
  String ownerPhone;
  String ownerAvatar;
}
