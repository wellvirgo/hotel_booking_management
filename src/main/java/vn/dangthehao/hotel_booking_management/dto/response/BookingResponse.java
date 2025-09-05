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
  BigDecimal totalPrice;
  BigDecimal depositAmount;
  LocalDateTime expiresAt;
  String depositPaymentUrl;
}
