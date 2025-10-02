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
public class AdminHotelListItemDTO extends HotelBaseInfoDTO {
  String ownerName;
  HotelStatus status;

  public AdminHotelListItemDTO(
      Long id,
      String hotelName,
      String ownerName,
      String address,
      String location,
      String thumbnail,
      float rating) {
    super(id, hotelName, address, location, thumbnail, rating);
    this.ownerName = ownerName;
  }
}
