package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.UserCrtResponse;
import vn.dangthehao.hotel_booking_management.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCrtRequest request);
    UserCrtResponse toUserCrtResponse(User user);
}
