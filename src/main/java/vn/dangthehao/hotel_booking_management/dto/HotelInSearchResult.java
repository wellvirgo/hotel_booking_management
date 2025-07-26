package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelInSearchResult {
    Long id;
    String hotelName;
    String address;
    String thumbnailUrl;
    float rating;
    int totalReviewCount;
    LowestPriceRoomType lowestPriceRoomType;
}
