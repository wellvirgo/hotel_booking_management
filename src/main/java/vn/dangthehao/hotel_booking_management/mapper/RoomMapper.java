package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.dangthehao.hotel_booking_management.dto.request.RoomCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.RoomCrtResponse;
import vn.dangthehao.hotel_booking_management.model.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

  @Mapping(target = "status", constant = "AVAILABLE")
  Room toRoom(RoomCrtRequest roomCrtRequest);

  RoomCrtResponse toRoomCrtResponse(Room room);
}
