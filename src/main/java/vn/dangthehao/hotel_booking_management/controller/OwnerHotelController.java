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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.service.HotelService;
import vn.dangthehao.hotel_booking_management.service.RoomTypeService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/owner/hotels")
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

    @PostMapping("{id}/room-types")
    public ResponseEntity<ApiResponse<RoomTypeCrtResponse>> createRoomType(
            @PathVariable(name = "id") Long hotelId,
            @Valid @RequestPart(name = "data") RoomTypeCrtRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> imageFiles) {
        ApiResponse<RoomTypeCrtResponse> apiResponse = roomTypeService.create(hotelId, request, imageFiles);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(apiResponse.getData().getId())
                .toUri();

        return ResponseEntity.created(location).body(apiResponse);
    }

    @GetMapping("/{id}/room-types")
    public ResponseEntity<ApiResponse<OwnerRoomTypesResponse>> listRoomTypes(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK).body(roomTypeService.getRoomTypesByHotelId(id, page, size));
    }

    @PutMapping("/{hotelId}/room-types/{roomTypeId}")
    public ResponseEntity<ApiResponse<RoomTypeUpdateResponse>> updateRoomType(
            @PathVariable(name = "hotelId") Long hotelId,
            @PathVariable(name = "roomTypeId") Long roomTypeId,
            @Valid @RequestPart(name = "data") RoomTypeUpdateRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> imageFiles) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roomTypeService.updateRoomType(roomTypeId, hotelId, request, imageFiles));
    }

    @GetMapping("/{hotelId}/room-types/{roomTypeId}")
    public ResponseEntity<ApiResponse<OwnerDetailRoomTypeResponse>> detailRoomType(
            @PathVariable(name = "hotelId") Long hotelId,
            @PathVariable(name = "roomTypeId") Long roomTypeId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomTypeService.detailRoomType(hotelId, roomTypeId));
    }
}
