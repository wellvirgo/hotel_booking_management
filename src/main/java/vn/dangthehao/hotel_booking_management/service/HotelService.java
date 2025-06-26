package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.DetailHotelResponse;
import vn.dangthehao.hotel_booking_management.dto.response.UnapprovedHotelListResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
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
    MailService mailService;

    static String BASE_AVATAR_URL = "http://localhost:8080/avatars/";

    public Hotel findById(Long id) {
        return hotelRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
    }

    public ApiResponse<Void> register(HotelRegistrationRequest request, Jwt jwt) {
        Hotel registeredHotel = hotelMapper.toHotel(request);
        Long ownerId = jwtUtil.getUserID(jwt);
        registeredHotel.setOwner(userService.findByID(ownerId));
        hotelRepository.save(registeredHotel);

        return responseGenerator.generateSuccessResponse("Your application will be handled soon!");
    }

    public ApiResponse<UnapprovedHotelListResponse> findUnapprovedHotels(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UnapprovedHotelDTO> unapprovedHotelDTOPage = hotelRepository.findUnapprovedHotels(pageable, HotelStatus.INACTIVE);

        UnapprovedHotelListResponse unapprovedHotelListResponse = UnapprovedHotelListResponse.builder()
                .unapprovedHotels(unapprovedHotelDTOPage.getContent())
                .currentPage(page)
                .totalPages(unapprovedHotelDTOPage.getTotalPages())
                .build();
        return responseGenerator.generateSuccessResponse("List of unapproved hotels", unapprovedHotelListResponse);
    }

    public ApiResponse<DetailHotelResponse> getDetailHotel(Long id) {
        Hotel hotel = findById(id);
        DetailHotelResponse response = hotelMapper.toDetailHotelResponse(hotel);
        response.setOwnerFullName(hotel.getOwner().getFullName());
        response.setOwnerEmail(hotel.getOwner().getEmail());
        response.setOwnerPhone(hotel.getOwner().getPhone());
        String avatarUrl = (hotel.getOwner().getAvatar() != null)
                ? BASE_AVATAR_URL + hotel.getOwner().getAvatar()
                : "";
        response.setOwnerAvatar(avatarUrl);

        return responseGenerator.generateSuccessResponse("Hotel detail", response);
    }

    public ApiResponse<Void> approveHotel(Long id) {
        Hotel hotel = findById(id);

        if (hotel.isApproved() || HotelStatus.REJECTED.equals(hotel.getStatus()))
            throw new AppException(ErrorCode.CAN_NOT_APPROVE_HOTEL);

        hotel.setApproved(true);
        hotel.setStatus(HotelStatus.MAINTENANCE);
        hotelRepository.save(hotel);
        String ownerEmail = hotel.getOwner().getEmail();
        mailService.sendApproveHotelEmailAsync(ownerEmail, hotel.getHotelName());

        return responseGenerator.generateSuccessResponse("Hotel is approved");
    }

    public ApiResponse<Void> rejectHotel(Long id) {
        Hotel hotel = findById(id);

        if (hotel.isApproved() || HotelStatus.REJECTED.equals(hotel.getStatus()))
            throw new AppException(ErrorCode.CAN_NOT_REJECT_HOTEL);

        hotel.setApproved(false);
        hotel.setStatus(HotelStatus.REJECTED);
        hotelRepository.save(hotel);
        String ownerEmail = hotel.getOwner().getEmail();
        mailService.sendRejectHotelEmailAsync(ownerEmail, hotel.getHotelName());

        return responseGenerator.generateSuccessResponse("Hotel is rejected");
    }
}
