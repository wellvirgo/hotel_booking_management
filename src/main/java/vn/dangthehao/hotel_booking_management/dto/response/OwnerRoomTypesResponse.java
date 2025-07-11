package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerRoomTypesResponse extends BaseFormListResponse {
    List<OwnerRoomTypeDTO> roomTypes;
}
