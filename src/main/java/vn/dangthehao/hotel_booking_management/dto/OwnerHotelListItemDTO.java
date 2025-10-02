package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerHotelListItemDTO extends HotelBaseInfoDTO {
  HotelStatus status;

  public OwnerHotelListItemDTO(
      Long id, String hotelName, String address, String location, String thumbnail, float rating) {
    super(id, hotelName, address, location, thumbnail, rating);
  }
}
