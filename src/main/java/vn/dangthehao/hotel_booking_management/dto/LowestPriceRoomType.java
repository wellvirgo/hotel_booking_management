package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

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
