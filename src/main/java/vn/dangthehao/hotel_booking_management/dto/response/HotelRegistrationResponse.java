package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;

import java.util.List;

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
    Float depositRate;
    Float depositDeadlineHours;
    String status;
    boolean isApproved;
    float rating;
    Long ownerId;
    List<OwnerRoomTypeDTO> roomTypes;
}
