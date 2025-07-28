package vn.dangthehao.hotel_booking_management.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeCrtResponse {
  Long id;
  Long hotelId;
  String name;
  String description;
  BigDecimal pricePerNight;
  int capacity;
  int numOfBeds;
  String bedType;
  int totalRooms;
  List<String> imageUrls;
  List<String> amenityNames;
}
