package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.UserCrtResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserListResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UserUpdateResponse;
import vn.dangthehao.hotel_booking_management.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCrtRequest request);

    UserCrtResponse toUserCrtResponse(User user);

    @Mapping(target = "avatar", ignore = true)
    UserUpdateResponse toUserUpdateResponse(User user);

    @Mapping(target = "avatar", ignore = true)
    UserListResponse toUserListResponse(User user);

    @Mapping(target = "avatar", ignore = true)
    UserResponse toUserResponse(User user);
}
