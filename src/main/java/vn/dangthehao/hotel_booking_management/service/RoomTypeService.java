package vn.dangthehao.hotel_booking_management.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.OwnerRoomTypeDTO;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeCrtRequest;
import vn.dangthehao.hotel_booking_management.dto.request.RoomTypeUpdateRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.ImageUrlMapper;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.AmenityRepository;
import vn.dangthehao.hotel_booking_management.repository.BookingRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomTypeService {
  RoomTypeRepository roomTypeRepository;
  BookingRepository bookingRepository;
  HotelService hotelService;
  RoomTypeMapper roomTypeMapper;
  RoomInventoryService roomInventoryService;
  AmenityRepository amenityRepository;
  UploadFileService uploadFileService;
  ImageUrlMapper imageUrlMapper;

  @NonFinal
  @Value("${base-url}")
  String baseUrl;

  @NonFinal
  @Value("${file.room-type-img-folder}")
  String roomTypeImgFolderName;

  @NonFinal
  @Value("${file.upload-folder}")
  String uploadFolder;

  @PreAuthorize("@hotelService.isOwner(#hotelId, #ownerId)")
  public ApiResponse<RoomTypeCrtResponse> create(
      Long hotelId, Long ownerId, RoomTypeCrtRequest request, List<MultipartFile> imageFiles) {
    Hotel hotel = hotelService.getConfigurableHotel(hotelId);
    RoomType roomType = buildRoomType(hotel, request, imageFiles);
    RoomType savedRoomType = roomTypeRepository.save(roomType);
    RoomTypeCrtResponse roomTypeCrtResponse = buildRoomTypeCrtResponse(savedRoomType, hotel);

    // Create some inventories in advance
    roomInventoryService.createRoomInventories(savedRoomType);

    return ApiResponseBuilder.success("Config room type successfully!", roomTypeCrtResponse);
  }

  public RoomType findById(Long id) {
    return roomTypeRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND, id));
  }

  public RoomType findActiveRoomTypeById(Long id) {
    return roomTypeRepository
        .findByIdAndActiveTrue(id)
        .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND, id));
  }

  @PreAuthorize("@hotelService.isOwner(#hotelId, #ownerId)")
  public OwnerRoomTypesResponse getRoomTypesForOwner(
      Long hotelId, Long ownerId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<OwnerRoomTypeDTO> ownerRoomTypeDTOPage =
        roomTypeRepository.findByHotelId(hotelId, pageable);

    List<OwnerRoomTypeDTO> roomTypes = ownerRoomTypeDTOPage.getContent();

    return OwnerRoomTypesResponse.builder()
        .roomTypes(roomTypes)
        .currentPage(page)
        .totalPages(ownerRoomTypeDTOPage.getTotalPages())
        .build();
  }

  @PreAuthorize("@hotelService.isOwner(#hotelId, #ownerId)")
  public RoomTypeUpdateResponse updateRoomType(
      Long roomTypeId,
      Long hotelId,
      Long ownerId,
      RoomTypeUpdateRequest request,
      List<MultipartFile> imageFiles) {
    RoomType roomTypeBeforeUpdate = findById(roomTypeId);
    Set<Amenity> amenities = amenityRepository.findByNameIn(request.getAmenityNames());
    mapRoomTypeUpdateRequestToRoomType(request, roomTypeBeforeUpdate);
    roomTypeBeforeUpdate.setAmenities(amenities);
    updateRoomTypeImages(roomTypeBeforeUpdate, imageFiles);

    return mapRoomTypeToRoomTypeUpdateResponse(roomTypeRepository.save(roomTypeBeforeUpdate));
  }

  @PreAuthorize("@hotelService.isOwner(#hotelId, #ownerId)")
  public OwnerDetailRoomTypeResponse detailRoomType(Long hotelId, Long roomTypeId, Long ownerId) {
    RoomType roomType = findById(roomTypeId);
    OwnerDetailRoomTypeResponse response = roomTypeMapper.toOwnerDetailRoomTypeResponse(roomType);
    response.setAmenityNames(convertToAmenityNames(roomType.getAmenities()));
    List<String> imageUrls = imageUrlMapper.toUrls(roomType.getImageNames(), roomTypeImgFolderName);
    response.setImageUrls(imageUrls);

    return response;
  }

  public Long getIdByBookingId(Long bookingId) {
    Optional<Long> optRoomTypeId = bookingRepository.findRoomTypeIdByBookingId(bookingId);
    if (optRoomTypeId.isEmpty()) {
      log.error("Cannot find room type id from booking with id {}", bookingId);
      throw new AppException(ErrorCode.BOOKING_NOT_FOUND, bookingId);
    }

    return optRoomTypeId.get();
  }

  private RoomType buildRoomType(
      Hotel hotel, RoomTypeCrtRequest request, List<MultipartFile> imageFiles) {
    Set<Amenity> amenities = amenityRepository.findByNameIn(request.getAmenityNames());
    RoomType roomType = roomTypeMapper.roomTypeCrtRequestToRoomType(request);
    roomType.setHotel(hotel);
    roomType.setAmenities(amenities);
    roomType.setActive(true);

    if (imageFiles != null) {
      List<MultipartFile> validImageFiles = getValidImageFiles(imageFiles);
      if (!validImageFiles.isEmpty()) roomType.setImageNames(saveRoomTypeImages(imageFiles));
    }

    return roomType;
  }

  private RoomTypeCrtResponse buildRoomTypeCrtResponse(RoomType savedRoomType, Hotel hotel) {
    RoomTypeCrtResponse roomTypeCrtResponse = roomTypeMapper.toRoomTypeCrtResponse(savedRoomType);
    roomTypeCrtResponse.setHotelId(hotel.getId());
    List<String> imageUrls =
        imageUrlMapper.toUrls(savedRoomType.getImageNames(), roomTypeImgFolderName);
    roomTypeCrtResponse.setImageUrls(imageUrls);
    roomTypeCrtResponse.setAmenityNames(convertToAmenityNames(savedRoomType.getAmenities()));

    return roomTypeCrtResponse;
  }

  private List<String> saveRoomTypeImages(List<MultipartFile> imageFiles) {
    List<String> imageNames = new ArrayList<>();
    for (MultipartFile imageFile : imageFiles) {
      String imageUrl = uploadFileService.saveFile(roomTypeImgFolderName, imageFile);
      imageNames.add(imageUrl.replace(String.format("%s/%s/", baseUrl, roomTypeImgFolderName), ""));
    }

    return imageNames;
  }

  private void mapRoomTypeUpdateRequestToRoomType(
      RoomTypeUpdateRequest request, RoomType roomType) {
    roomType.setName(request.getName());
    roomType.setDescription(request.getDescription());
    roomType.setPricePerNight(request.getPricePerNight());
    roomType.setCapacity(request.getCapacity());
    roomType.setNumOfBeds(request.getNumOfBeds());
    roomType.setBedType(request.getBedType());
    roomType.setTotalRooms(request.getTotalRooms());
  }

  private void updateRoomTypeImages(RoomType roomType, List<MultipartFile> imageFiles) {
    if (imageFiles == null) return;

    List<MultipartFile> validImageFiles = getValidImageFiles(imageFiles);
    if (!validImageFiles.isEmpty()) {
      for (String oldImage : roomType.getImageNames()) {
        String pathImage = String.format("%s%s/%s", uploadFolder, roomTypeImgFolderName, oldImage);
        File fileImage = new File(pathImage);
        System.out.println("Deleting image");
        if (fileImage.exists()) fileImage.delete();
      }
      System.out.println("Saving image");
      roomType.setImageNames(saveRoomTypeImages(validImageFiles));
    }
  }

  private List<MultipartFile> getValidImageFiles(List<MultipartFile> imageFiles) {
    Predicate<MultipartFile> isValidImageFile =
        file -> file != null && !file.isEmpty() && StringUtils.hasText(file.getOriginalFilename());

    return imageFiles.stream().filter(isValidImageFile).toList();
  }

  private RoomTypeUpdateResponse mapRoomTypeToRoomTypeUpdateResponse(RoomType roomType) {
    RoomTypeUpdateResponse response = roomTypeMapper.toRoomTypeUpdateResponse(roomType);
    List<String> imageUrls = imageUrlMapper.toUrls(roomType.getImageNames(), roomTypeImgFolderName);
    response.setImageUrls(imageUrls);
    response.setAmenityNames(convertToAmenityNames(roomType.getAmenities()));

    return response;
  }

  private List<String> convertToAmenityNames(Set<Amenity> amenities) {
    return amenities.stream().map(Amenity::getName).toList();
  }
}
