package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.request.SearchHotelRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.SearchHotelResponse;
import vn.dangthehao.hotel_booking_management.service.HotelService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
  HotelService hotelService;

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<SearchHotelResponse>> searchHotel(
      @Valid @ModelAttribute SearchHotelRequest request,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(hotelService.searchHotels(request, page, size));
  }
}
