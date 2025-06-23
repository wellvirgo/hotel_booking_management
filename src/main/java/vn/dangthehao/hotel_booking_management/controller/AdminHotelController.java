package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.DetailHotelResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UnapprovedHotelListResponse;
import vn.dangthehao.hotel_booking_management.service.HotelService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/admin/hotels")
public class AdminHotelController {
    HotelService hotelService;

    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<UnapprovedHotelListResponse>> listUnapprovedHotels(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.findUnapprovedHotels(page, size));
    }

    @GetMapping("/pending-approval/{id}")
    public ResponseEntity<ApiResponse<DetailHotelResponse>> getUnapprovedHotel(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getDetailHotel(id));
    }
}
