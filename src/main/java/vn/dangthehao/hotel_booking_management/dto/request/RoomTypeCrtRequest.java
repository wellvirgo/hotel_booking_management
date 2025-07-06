package vn.dangthehao.hotel_booking_management.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeCrtRequest {
    Long hotelId;
    String name;
    String description;
    BigDecimal pricePerNight;
    int capacity;
    int numOfBeds;
    String bedType;
    int totalRooms;
    Set<String> amenityNames;
}
