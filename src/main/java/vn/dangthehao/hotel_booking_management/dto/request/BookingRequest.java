package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.annotations.ValidDateRange;
import vn.dangthehao.hotel_booking_management.annotations.ValidGuestInfo;
import vn.dangthehao.hotel_booking_management.dto.HasDateRange;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidDateRange
@ValidGuestInfo
public class BookingRequest implements HasDateRange {
  Long hotelId;
  Long roomTypeId;

  @NotNull(message = "Check-in date is required")
  @FutureOrPresent(message = "Check-in date must be today or later")
  LocalDate checkIn;

  @NotNull(message = "Check-out date is required")
  @Future(message = "Check-out date must be in the future")
  LocalDate checkOut;

  String guestName;
  String guestPhone;
  String guestEmail;

  @Min(value = 1, message = "The number of rooms must be greater than or equal to 1")
  int numRooms;

  String clientIp;
}
