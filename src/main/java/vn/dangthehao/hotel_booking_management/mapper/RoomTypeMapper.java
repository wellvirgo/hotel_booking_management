package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.dangthehao.hotel_booking_management.dto.LowestPriceRoomType;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.OwnerDetailRoomTypeResponse;
import vn.dangthehao.hotel_booking_management.dto.response.RoomTypeCrtResponse;
import vn.dangthehao.hotel_booking_management.dto.response.RoomTypeUpdateResponse;
import vn.dangthehao.hotel_booking_management.model.RoomType;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {
  RoomType roomTypeCrtRequestToRoomType(RoomTypeCrtRequest roomTypeCrtRequest);

  @Mapping(target = "imageUrls", ignore = true)
  RoomTypeUpdateResponse toRoomTypeUpdateResponse(RoomType roomType);

  OwnerDetailRoomTypeResponse toOwnerDetailRoomTypeResponse(RoomType roomType);

  RoomTypeCrtResponse toRoomTypeCrtResponse(RoomType roomType);

  LowestPriceRoomType toLowestPriceRoomType(RoomType roomType);
}
