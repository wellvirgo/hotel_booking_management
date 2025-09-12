package vn.dangthehao.hotel_booking_management.dto.response;

import java.time.LocalTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRegistrationResponse {
  Long id;
  String hotelName;
  String address;
  String location;
  String description;
  String thumbnailUrl;
  float depositRate;
  long depositDeadlineMinutes;
  LocalTime checkInTime;
  LocalTime checkOutTime;
  String status;
  boolean isApproved;
  float rating;
  Long ownerId;
  List<OwnerRoomTypeDTO> roomTypes;
}
