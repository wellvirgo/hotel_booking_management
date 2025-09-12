package vn.dangthehao.hotel_booking_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingResponse {
  Long id;
  String bookingCode;
  BookingStatus status;
  LocalDateTime checkIn;
  LocalDateTime checkOut;
  BigDecimal totalPrice;
  BigDecimal depositAmount;
  boolean depositRequired;
  LocalDateTime expiresAt;
  String depositPaymentUrl;
  LocalDateTime createdAt;
}
