package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCrtResponse {
  Long id;
  String roomNumber;
  String roomTypeName;
  String description;
  boolean active;
  String status;
}
