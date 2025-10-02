package vn.dangthehao.hotel_booking_management.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.service.HotelService;
import vn.dangthehao.hotel_booking_management.service.RoomTypeService;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/owner/hotels")
public class OwnerHotelController {
  HotelService hotelService;
  RoomTypeService roomTypeService;
  JwtService jwtService;

  @PostMapping
  public ResponseEntity<ApiResponse<HotelRegistrationResponse>> register(
      @Valid @RequestPart(name = "data") HotelRegistrationRequest request,
      @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail,
      @AuthenticationPrincipal Jwt jwt) {
    Long ownerId = jwtService.getUserId(jwt);

    HotelRegistrationResponse hotelRegistrationResponse =
        hotelService.register(request, thumbnail, ownerId);

    String message = "Your application will be handled soon!";
    ApiResponse<HotelRegistrationResponse> apiResponse =
        ApiResponseBuilder.success(message, hotelRegistrationResponse);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(apiResponse.getData().getId())
            .toUri();

    return ResponseEntity.created(location).body(apiResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<OwnerHotelListResponse>> getHotelList(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "5") int size,
      @RequestParam(name = "status", defaultValue = "ACTIVE") HotelStatus status) {
    Long ownerId = jwtService.getUserId(jwt);

    String message = String.format("List of %d %s hotels", size, status);
    ApiResponse<OwnerHotelListResponse> apiResponse =
        ApiResponseBuilder.success(
            message, hotelService.getHotelsByStatusForOwner(ownerId, status, page, size));

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @PostMapping("{id}/room-types")
  public ResponseEntity<ApiResponse<RoomTypeCrtResponse>> createRoomType(
      @PathVariable(name = "id") Long hotelId,
      @Valid @RequestPart(name = "data") RoomTypeCrtRequest request,
      @RequestPart(name = "images", required = false) List<MultipartFile> imageFiles,
      @AuthenticationPrincipal Jwt jwt) {
    Long ownerId = jwtService.getUserId(jwt);

    ApiResponse<RoomTypeCrtResponse> apiResponse =
        roomTypeService.create(hotelId, ownerId, request, imageFiles);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(apiResponse.getData().getId())
            .toUri();

    return ResponseEntity.created(location).body(apiResponse);
  }

  @GetMapping("/{id}/room-types")
  public ResponseEntity<ApiResponse<OwnerRoomTypesResponse>> listRoomTypes(
      @PathVariable(name = "id") Long hotelId,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "5") int size,
      @AuthenticationPrincipal Jwt jwt) {
    Long ownerId = jwtService.getUserId(jwt);

    String message = String.format("List room type in hotel %d", hotelId);
    OwnerRoomTypesResponse data =
        roomTypeService.getRoomTypesForOwner(hotelId, ownerId, page, size);
    ApiResponse<OwnerRoomTypesResponse> apiResponse = ApiResponseBuilder.success(message, data);

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @PutMapping("/{hotelId}/room-types/{roomTypeId}")
  public ResponseEntity<ApiResponse<RoomTypeUpdateResponse>> updateRoomType(
      @PathVariable(name = "hotelId") Long hotelId,
      @PathVariable(name = "roomTypeId") Long roomTypeId,
      @Valid @RequestPart(name = "data") RoomTypeUpdateRequest request,
      @RequestPart(name = "images", required = false) List<MultipartFile> imageFiles,
      @AuthenticationPrincipal Jwt jwt) {
    Long ownerId = jwtService.getUserId(jwt);

    String message = String.format("Updated room type %d in hotel %d", roomTypeId, hotelId);
    RoomTypeUpdateResponse data =
        roomTypeService.updateRoomType(roomTypeId, hotelId, ownerId, request, imageFiles);
    ApiResponse<RoomTypeUpdateResponse> apiResponse = ApiResponseBuilder.success(message, data);

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }

  @GetMapping("/{hotelId}/room-types/{roomTypeId}")
  public ResponseEntity<ApiResponse<OwnerDetailRoomTypeResponse>> detailRoomType(
      @PathVariable(name = "hotelId") Long hotelId,
      @PathVariable(name = "roomTypeId") Long roomTypeId,
      @AuthenticationPrincipal Jwt jwt) {
    Long ownerId = jwtService.getUserId(jwt);

    String message = String.format("Detail room type %d in hotel %d", roomTypeId, hotelId);
    OwnerDetailRoomTypeResponse data = roomTypeService.detailRoomType(hotelId, roomTypeId, ownerId);
    ApiResponse<OwnerDetailRoomTypeResponse> apiResponse =
        ApiResponseBuilder.success(message, data);

    return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
  }
}
