package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.response.DetailHotelResponse;
import vn.dangthehao.hotel_booking_management.dto.response.HotelRegistrationResponse;
import vn.dangthehao.hotel_booking_management.model.Hotel;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    @Mapping(target = "status", constant = "INACTIVE")
    Hotel toHotel(HotelRegistrationRequest request);

    DetailHotelResponse toDetailHotelResponse(Hotel hotel);

    HotelRegistrationResponse toHotelRegistrationRequest(Hotel hotel);
}
