package vn.dangthehao.hotel_booking_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerDetailRoomTypeResponse {
  Long id;
  String name;
  String description;
  BigDecimal pricePerNight;
  int capacity;
  int numOfBeds;
  String bedType;
  int totalRooms;
  LocalDateTime createdAt;
  List<String> imageUrls;
  List<String> amenityNames;
}
