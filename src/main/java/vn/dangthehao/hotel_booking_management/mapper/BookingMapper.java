package vn.dangthehao.hotel_booking_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.dangthehao.hotel_booking_management.dto.response.BookingResponse;
import vn.dangthehao.hotel_booking_management.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
  @Mapping(target = "expiresAt", source = "depositDeadline")
  BookingResponse toBookingResponse(Booking booking);
}
