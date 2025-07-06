package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.OwnerHotelsResponse;
import vn.dangthehao.hotel_booking_management.service.HotelService;
import vn.dangthehao.hotel_booking_management.service.RoomTypeService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/owner/hotels")
public class OwnerHotelController {
    HotelService hotelService;
    RoomTypeService roomTypeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerHotel(
            @Valid @RequestBody HotelRegistrationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.register(request, jwt));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<OwnerHotelsResponse>> findAllHotels(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "isApproved", defaultValue = "true") String isApproved,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.findHotelsByOwner(jwt, isApproved, page, size));
    }

    @PostMapping("/room-types")
    public ResponseEntity<ApiResponse<Void>> createRoomType(@RequestBody RoomTypeCrtRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeService.create(request));
    }

}
