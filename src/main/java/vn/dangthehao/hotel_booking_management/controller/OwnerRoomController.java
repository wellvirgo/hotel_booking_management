package vn.dangthehao.hotel_booking_management.controller;

import java.net.URI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.dangthehao.hotel_booking_management.dto.request.RoomCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.RoomCrtResponse;
import vn.dangthehao.hotel_booking_management.service.RoomService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/owner/rooms")
public class OwnerRoomController {
  RoomService roomService;

  @PostMapping
  public ResponseEntity<ApiResponse<RoomCrtResponse>> createRoom(
      @RequestBody RoomCrtRequest request) {
    ApiResponse<RoomCrtResponse> apiResponse = roomService.createRoom(request);

    // Add location to header to be more RESTFull for resource creation api
    Long roomId = apiResponse.getData().getId();
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(roomId)
            .toUri();

    return ResponseEntity.created(location).body(apiResponse);
  }
}
