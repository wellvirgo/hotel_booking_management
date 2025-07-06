package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.AmenityRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomTypeService {
    RoomTypeRepository roomTypeRepository;
    HotelService hotelService;
    RoomTypeMapper roomTypeMapper;
    RoomInventoryService roomInventoryService;
    ResponseGenerator responseGenerator;
    AmenityRepository amenityRepository;

    public ApiResponse<Void> create(RoomTypeCrtRequest request) {
        Hotel hotel = hotelService.findById(request.getHotelId());
        if (!hotel.isApproved())
            throw new AppException(ErrorCode.HOTEL_NOT_APPROVED);

        Set<Amenity> amenities = amenityRepository.findByNameIn(request.getAmenityNames());
        RoomType roomType = roomTypeMapper.roomTypeCrtRequestToRoomType(request);
        roomType.setHotel(hotel);
        roomType.setAmenities(amenities);
        roomType.setActive(true);
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // Tạo trước 6 tháng trống tương ứng trong RoomInventory
        roomInventoryService.createRoomInventories(savedRoomType);

        return responseGenerator.generateSuccessResponse("Config room type successfully!");
    }


}
