package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.annotations.ValidDateRange;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidDateRange
public class SearchHotelRequest {
  @NotNull(message = "Check-in date is required")
  @FutureOrPresent(message = "Check-in date must be today or later")
  LocalDate checkIn;

  @NotNull(message = "Check-out date is required")
  @Future(message = "Check-out date must be in the future")
  LocalDate checkOut;

  @NotBlank(message = "Location is required")
  String location;

  @Min(value = 1, message = "The number of guests must be greater than or equal to 1")
  int numGuests;

  @Min(value = 1, message = "The number of rooms must be greater than or equal to 1")
  int numRooms;
}
