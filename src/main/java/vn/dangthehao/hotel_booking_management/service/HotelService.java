package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.mapper.HotelMapper;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.repository.HotelRepository;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class HotelService {
    HotelMapper hotelMapper;
    HotelRepository hotelRepository;
    JwtUtil jwtUtil;
    ResponseGenerator responseGenerator;
    UserService userService;

    public ApiResponse<Void> register(HotelRegistrationRequest request, Jwt jwt) {
        Hotel registeredHotel = hotelMapper.toHotel(request);
        Long ownerId = jwtUtil.getUserID(jwt);
        registeredHotel.setOwner(userService.findByID(ownerId));
        hotelRepository.save(registeredHotel);

        return responseGenerator.generateSuccessResponse("Your application will be handled soon!");
    }
}
