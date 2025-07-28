package vn.dangthehao.hotel_booking_management.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.HotelInSearchResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchHotelResponse extends BaseFormListResponse {
  LocalDate checkInDate;
  LocalDate checkOutDate;
  String location;
  int requiredGuests;
  int requiredRooms;
  List<HotelInSearchResult> hotels;
}
