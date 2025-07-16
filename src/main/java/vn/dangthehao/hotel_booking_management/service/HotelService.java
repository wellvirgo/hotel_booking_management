package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.HotelMapper;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.repository.HotelRepository;
import vn.dangthehao.hotel_booking_management.util.ImageNameToUrlMapper;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    UploadFileService uploadFileService;
    ImageNameToUrlMapper imageNameToUrlMapper;

    static String BASE_AVATAR_URL = "http://localhost:8080/avatars/";

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    @NonFinal
    @Value("${file.hotel_img_folder_name}")
    String hotelImgFolderName;

    public Hotel findById(Long id) {
        return hotelRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
    }

    public ApiResponse<HotelRegistrationResponse> register(HotelRegistrationRequest request,
                                                           MultipartFile thumbnailFile,
                                                           Jwt jwt) {
        Hotel registeredHotel = createHotelFromRequest(request, thumbnailFile, jwt);
        Hotel savedHotel = hotelRepository.save(registeredHotel);
        HotelRegistrationResponse response = buildHotelRegistrationResponse(savedHotel, jwtUtil.getUserID(jwt));

        return responseGenerator.generateSuccessResponse("Your application will be handled soon!", response);
    }

    public ApiResponse<UnapprovedHotelsResponse> findUnapprovedHotels(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UnapprovedHotelDTO> unapprovedHotelDTOPage = hotelRepository.findUnapprovedHotels(pageable, HotelStatus.INACTIVE);

        UnapprovedHotelsResponse unapprovedHotelsResponse = UnapprovedHotelsResponse.builder()
                .unapprovedHotels(unapprovedHotelDTOPage.getContent())
                .currentPage(page)
                .totalPages(unapprovedHotelDTOPage.getTotalPages())
                .build();
        return responseGenerator.generateSuccessResponse("List of unapproved hotels", unapprovedHotelsResponse);
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

    public ApiResponse<OwnerHotelsResponse> findHotelsByOwner(Jwt jwt, String isApproved, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OwnerHotelItemDTO> ownerHotelItemDTOPage = null;
        OwnerHotelsResponse ownerHotelsResponse = null;
        if ("false".equals(isApproved)) {
            ownerHotelItemDTOPage = hotelRepository.findUnApprovedHotelsByOwner(pageable, jwtUtil.getUserID(jwt));
            ownerHotelsResponse = OwnerHotelsResponse.builder()
                    .ownerHotelItems(ownerHotelItemDTOPage.getContent())
                    .currentPage(page)
                    .totalPages(ownerHotelItemDTOPage.getTotalPages())
                    .build();

            return responseGenerator.generateSuccessResponse("List of owner unapproved hotels", ownerHotelsResponse);
        }

        ownerHotelItemDTOPage = hotelRepository.findApprovedHotelsByOwner(pageable, jwtUtil.getUserID(jwt));
        ownerHotelsResponse = OwnerHotelsResponse.builder()
                .ownerHotelItems(ownerHotelItemDTOPage.getContent())
                .currentPage(page)
                .totalPages(ownerHotelItemDTOPage.getTotalPages())
                .build();

        return responseGenerator.generateSuccessResponse("List of approved hotels", ownerHotelsResponse);
    }

    public boolean isOwner(Long hotelId, Jwt jwt) {
        Long ownerId = findById(hotelId).getOwner().getId();
        return ownerId.equals(jwtUtil.getUserID(jwt));
    }

    private Hotel createHotelFromRequest(HotelRegistrationRequest request, MultipartFile thumbnailFile, Jwt jwt) {
        Hotel registeredHotel = hotelMapper.toHotel(request);
        Long ownerId = jwtUtil.getUserID(jwt);
        registeredHotel.setOwner(userService.findByID(ownerId));

        if (validateThumbnailFile(thumbnailFile)) {
            String thumbnail = uploadFileService.saveFile(hotelImgFolderName, thumbnailFile)
                    .replace(String.format("%s/%s/", baseUrl, hotelImgFolderName), "");
            registeredHotel.setThumbnail(thumbnail);
        }

        return registeredHotel;
    }

    private boolean validateThumbnailFile(MultipartFile thumbnailFile) {
        return thumbnailFile != null
                && !thumbnailFile.isEmpty()
                && StringUtils.hasText(thumbnailFile.getOriginalFilename());
    }

    private HotelRegistrationResponse buildHotelRegistrationResponse(Hotel hotel, Long ownerId) {
        HotelRegistrationResponse response = hotelMapper.toHotelRegistrationRequest(hotel);
        response.setOwnerId(ownerId);
        response.setApproved(hotel.isApproved());
        response.setThumbnailUrl(imageNameToUrlMapper.toUrl(hotel.getThumbnail(), this.hotelImgFolderName));
        response.setRoomTypes(Collections.emptyList());

        return response;
    }
}
