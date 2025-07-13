package vn.dangthehao.hotel_booking_management.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.OwnerDetailRoomTypeResponse;
import vn.dangthehao.hotel_booking_management.dto.response.OwnerRoomTypesResponse;
import vn.dangthehao.hotel_booking_management.dto.response.RoomTypeUpdateResponse;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.AmenityRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ImageNameToUrlMapper;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
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
    ImageNameToUrlMapper imageNameToUrlMapper;

    @NonFinal
    @Value("${base_url}")
    String baseUrl;

    @NonFinal
    @Value("${file.room_type_img_folder_name}")
    String roomTypeImgFolderName;

    @NonFinal
    @Value("${file.upload_folder}")
    String uploadFolder;

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
        roomType.setImageNames(saveRoomTypeImages(imageFiles));
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

    @PreAuthorize("@hotelService.isOwner(#hotelId, authentication.principal)")
    public ApiResponse<RoomTypeUpdateResponse> updateRoomType(Long roomTypeId,
                                                              Long hotelId,
                                                              RoomTypeUpdateRequest request,
                                                              List<MultipartFile> imageFiles) {
        RoomType roomTypeBeforeUpdate = findById(roomTypeId);
        Set<Amenity> amenities = amenityRepository.findByNameIn(request.getAmenityNames());
        mapRoomTypeUpdateRequestToRoomType(request, roomTypeBeforeUpdate);
        roomTypeBeforeUpdate.setAmenities(amenities);
        updateRoomTypeImages(roomTypeBeforeUpdate, imageFiles);
        RoomTypeUpdateResponse response = mapRoomTypeToRoomTypeUpdateResponse(
                roomTypeRepository.save(roomTypeBeforeUpdate));

        return responseGenerator.generateSuccessResponse("Update room type successfully!", response);
    }

    @PreAuthorize("@hotelService.isOwner(#hotelId, authentication.principal)")
    public ApiResponse<OwnerDetailRoomTypeResponse> detailRoomType(Long hotelId, Long roomTypeId) {
        RoomType roomType = findById(roomTypeId);
        OwnerDetailRoomTypeResponse response = roomTypeMapper.toOwnerDetailRoomTypeResponse(roomType);
        response.setAmenityNames(convertToAmenityNames(roomType.getAmenities()));
        List<String> imageUrls = imageNameToUrlMapper.toUrls(roomType.getImageNames(), roomTypeImgFolderName);
        response.setImageUrls(imageUrls);

        String message = String.format("Detail of information about room type %s", roomType.getName());
        return responseGenerator.generateSuccessResponse(message, response);
    }

    private List<String> saveRoomTypeImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            String imageUrl = uploadFileService.saveFile(roomTypeImgFolderName, imageFile);
            imageUrls.add(imageUrl.replace(String.format("%s/%s/", baseUrl, roomTypeImgFolderName), ""));
        }

        return imageUrls;
    }

    private void mapRoomTypeUpdateRequestToRoomType(RoomTypeUpdateRequest request, RoomType roomType) {
        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setPricePerNight(request.getPricePerNight());
        roomType.setCapacity(request.getCapacity());
        roomType.setNumOfBeds(request.getNumOfBeds());
        roomType.setBedType(request.getBedType());
        roomType.setTotalRooms(request.getTotalRooms());
    }

    private void updateRoomTypeImages(RoomType roomType, List<MultipartFile> imageFiles) {
        if (!imageFiles.isEmpty()) {
            for (String oldImage : roomType.getImageNames()) {
                String pathImage = String.format("%s%s/%s",
                        uploadFolder,
                        roomTypeImgFolderName,
                        oldImage);
                File fileImage = new File(pathImage);
                if (fileImage.exists())
                    fileImage.delete();
            }
            roomType.setImageNames(saveRoomTypeImages(imageFiles));
        }
    }

    private RoomTypeUpdateResponse mapRoomTypeToRoomTypeUpdateResponse(RoomType roomType) {
        RoomTypeUpdateResponse response = roomTypeMapper.toRoomTypeUpdateResponse(roomType);
        List<String> imageUrls = imageNameToUrlMapper.toUrls(roomType.getImageNames(), roomTypeImgFolderName);
        response.setImageUrls(imageUrls);
        response.setAmenityNames(convertToAmenityNames(roomType.getAmenities()));

        return response;
    }

    private List<String> convertToAmenityNames(Set<Amenity> amenities) {
        return amenities.stream()
                .map(Amenity::getName)
                .toList();
    }
}
