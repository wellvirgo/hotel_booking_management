package vn.dangthehao.hotel_booking_management.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.BookingResponse;
import vn.dangthehao.hotel_booking_management.service.BookingService;
import vn.dangthehao.hotel_booking_management.util.IPUtil;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1")
public class BookingController {
  BookingService bookingService;

  @PostMapping("/hotels/{hotelId}/room-types/{roomTypeId}/bookings")
  public ResponseEntity<ApiResponse<BookingResponse>> bookHotel(
      @PathVariable Long hotelId,
      @PathVariable Long roomTypeId,
      @Valid @RequestBody BookingRequest bookingRequest,
      HttpServletRequest httpServletRequest) {
    bookingRequest.setHotelId(hotelId);
    bookingRequest.setRoomTypeId(roomTypeId);
    bookingRequest.setClientIp(IPUtil.getClientIP(httpServletRequest));

    return ResponseEntity.ok(bookingService.holdReservation(bookingRequest));
  }
}
