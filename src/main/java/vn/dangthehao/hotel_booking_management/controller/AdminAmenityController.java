package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.hotel_booking_management.dto.request.AmenityCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.service.AmenityService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/admin/amenities")
public class AdminAmenityController {
  AmenityService amenityService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> create(@RequestBody AmenityCrtRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(amenityService.createAmenity(request));
  }
}
