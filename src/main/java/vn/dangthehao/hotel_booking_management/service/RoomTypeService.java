package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.OwnerRoomTypesResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.AmenityRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.util.ArrayList;
import java.util.List;
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
    UploadFileService uploadFileService;

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    @NonFinal
    @Value("${file.room_type_img_folder_name}")
    String roomTypeImgFolderName;

    @PreAuthorize("@hotelService.isOwner(#request.hotelId, authentication.principal)")
    public ApiResponse<Void> create(RoomTypeCrtRequest request, List<MultipartFile> imageFiles) {
        Hotel hotel = hotelService.findById(request.getHotelId());
        if (!hotel.isApproved())
            throw new AppException(ErrorCode.HOTEL_NOT_APPROVED);

        Set<Amenity> amenities = amenityRepository.findByNameIn(request.getAmenityNames());
        RoomType roomType = roomTypeMapper.roomTypeCrtRequestToRoomType(request);
        roomType.setHotel(hotel);
        roomType.setAmenities(amenities);
        roomType.setActive(true);
        roomType.setImageUrls(saveRoomTypeImages(imageFiles));
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // Tạo trước 6 tháng trống tương ứng trong RoomInventory
        roomInventoryService.createRoomInventories(savedRoomType);

        return responseGenerator.generateSuccessResponse("Config room type successfully!");
    }

    public RoomType findById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND, id));
    }

    @PreAuthorize("@hotelService.isOwner(#hotelId, authentication.principal)")
    public ApiResponse<OwnerRoomTypesResponse> getRoomTypesByHotelId(Long hotelId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OwnerRoomTypeDTO> ownerRoomTypeDTOPage = roomTypeRepository.findByHotelId(hotelId, pageable);
        List<OwnerRoomTypeDTO> ownerRoomTypeDTOList = ownerRoomTypeDTOPage.getContent();
        OwnerRoomTypesResponse response = OwnerRoomTypesResponse.builder()
                .roomTypes(ownerRoomTypeDTOList)
                .currentPage(page)
                .totalPages(ownerRoomTypeDTOPage.getTotalPages())
                .build();

        return responseGenerator.generateSuccessResponse("List room type in this hotel", response);
    }

    private List<String> saveRoomTypeImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            String imageUrl = uploadFileService.saveFile(roomTypeImgFolderName, imageFile);
            imageUrls.add(imageUrl.replace(String.format("%s/%s/", baseUrl, roomTypeImgFolderName), ""));
        }

        return imageUrls;
    }
}
