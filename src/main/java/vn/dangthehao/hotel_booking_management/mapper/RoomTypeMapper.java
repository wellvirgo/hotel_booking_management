package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.model.RoomType;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {
    RoomType roomTypeCrtRequestToRoomType(RoomTypeCrtRequest roomTypeCrtRequest);
}
