package vn.dangthehao.hotel_booking_management.service;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.RoomCrtResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.RoomMapper;
import vn.dangthehao.hotel_booking_management.model.Room;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.RoomRepository;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomService {
  RoomRepository roomRepository;
  RoomMapper roomMapper;
  RoomTypeService roomTypeService;
  ResponseGenerator responseGenerator;

  public ApiResponse<RoomCrtResponse> createRoom(RoomCrtRequest roomCrtRequest) {
    Long roomTypeId = roomCrtRequest.getRoomTypeId();
    RoomType roomType = roomTypeService.findById(roomTypeId);
    if (roomRepository.countByRoomTypeId(roomTypeId) >= roomType.getTotalRooms())
      throw new AppException(ErrorCode.QUANTITY_ROOM_EXCEEDED);

    Room room = roomMapper.toRoom(roomCrtRequest);
    room.setActive(true);
    room.setRoomType(roomType);

    RoomCrtResponse roomCrtResponse = generateRoomCrtResponse(roomRepository.save(room), roomType);
    return responseGenerator.generateSuccessResponse("Create room successfully", roomCrtResponse);
  }

  public List<Room> getAvailableRoomsForBooking(BookingRequest bookingRequest) {
    Pageable limit = PageRequest.of(0, bookingRequest.getNumRooms());
    return roomRepository.findByRoomTypeIdAndStatusOrderByRoomNumberAsc(
        bookingRequest.getRoomTypeId(), RoomStatus.AVAILABLE, limit);
  }

  public void updateRoomStatuses(List<Long> roomIds, RoomStatus status) {
    roomRepository.updateRoomStatusByIds(roomIds, status);
  }

  private RoomCrtResponse generateRoomCrtResponse(Room room, RoomType roomType) {
    RoomCrtResponse roomCrtResponse = roomMapper.toRoomCrtResponse(room);
    roomCrtResponse.setRoomTypeName(roomType.getName());

    return roomCrtResponse;
  }
}
