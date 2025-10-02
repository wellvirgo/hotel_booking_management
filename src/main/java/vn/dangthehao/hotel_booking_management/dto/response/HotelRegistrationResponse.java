package vn.dangthehao.hotel_booking_management.dto.response;

import java.time.LocalTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.HotelBaseInfoDTO;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRegistrationResponse extends HotelBaseInfoDTO {
  String description;
  float depositRate;
  long depositDeadlineMinutes;
  LocalTime checkInTime;
  LocalTime checkOutTime;
  String status;
  List<OwnerRoomTypeDTO> roomTypes;
}
