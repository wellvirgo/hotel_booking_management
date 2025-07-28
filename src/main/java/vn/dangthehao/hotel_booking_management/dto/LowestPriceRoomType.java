package vn.dangthehao.hotel_booking_management.dto;

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
public class LowestPriceRoomType {
  Long roomTypeId;
  String name;
  BigDecimal pricePerNight;
  int capacity;
  List<String> amenityNames;
}
