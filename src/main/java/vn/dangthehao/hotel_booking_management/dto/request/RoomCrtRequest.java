package vn.dangthehao.hotel_booking_management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCrtRequest {
    Long roomTypeId;
    String roomNumber;
    String description;
}
