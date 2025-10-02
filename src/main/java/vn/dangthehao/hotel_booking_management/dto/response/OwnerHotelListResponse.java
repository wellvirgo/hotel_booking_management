package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelListItemDTO;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerHotelListResponse extends BaseFormListResponse {
    List<OwnerHotelListItemDTO> hotelList;
}
