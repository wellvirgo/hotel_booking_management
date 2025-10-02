package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.response.AdminDetailHotelResponse;
import vn.dangthehao.hotel_booking_management.dto.response.AdminHotelListResponse;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.service.HotelService;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/admin/hotels")
public class AdminHotelController {
  HotelService hotelService;

  @GetMapping
  public ResponseEntity<ApiResponse<AdminHotelListResponse>> getHotelList(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "5") int size,
      @RequestParam(name = "status", defaultValue = "ACTIVE") HotelStatus status) {
    String message = String.format("List of %d %s hotel", size, status);

    ApiResponse<AdminHotelListResponse> apiResponse =
        ApiResponseBuilder.success(
            message, hotelService.getHotelsByStatusForAdmin(status, page, size));

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AdminDetailHotelResponse>> detailHotel(
      @PathVariable(name = "id") long id) {
    ApiResponse<AdminDetailHotelResponse> apiResponse =
        ApiResponseBuilder.success(
            String.format("Detail of hotel %d", id), hotelService.getDetail(id));

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approveHotel(@PathVariable(name = "id") long id) {
    hotelService.approveHotel(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponseBuilder.success("Hotel is approved"));
  }

  @PostMapping("/{id}/reject")
  public ResponseEntity<ApiResponse<Void>> rejectHotel(@PathVariable(name = "id") long id) {
    hotelService.rejectHotel(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponseBuilder.success("Hotel is rejected"));
  }
}
