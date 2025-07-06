package vn.dangthehao.hotel_booking_management.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.DetailHotelResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UnapprovedHotelsResponse;
import vn.dangthehao.hotel_booking_management.service.HotelService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/admin/hotels")
public class AdminHotelController {
    HotelService hotelService;

    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<UnapprovedHotelsResponse>> listUnapprovedHotels(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.findUnapprovedHotels(page, size));
    }

    @GetMapping("/pending-approval/{id}")
    public ResponseEntity<ApiResponse<DetailHotelResponse>> getUnapprovedHotel(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getDetailHotel(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveHotel(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.approveHotel(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectHotel(@PathVariable(name = "id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.rejectHotel(id));
    }
}
