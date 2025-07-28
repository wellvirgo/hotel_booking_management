package vn.dangthehao.hotel_booking_management.dto.response;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class OwnerHotelsResponse extends BaseFormListResponse {
  List<OwnerHotelItemDTO> ownerHotelItems;
}
