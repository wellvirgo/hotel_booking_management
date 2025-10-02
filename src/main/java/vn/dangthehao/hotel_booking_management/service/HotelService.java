package vn.dangthehao.hotel_booking_management.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.AdminHotelListItemDTO;
import vn.dangthehao.hotel_booking_management.dto.HotelInSearchResult;
import vn.dangthehao.hotel_booking_management.dto.LowestPriceRoomType;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelListItemDTO;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.request.SearchHotelRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.HotelMapper;
import vn.dangthehao.hotel_booking_management.mapper.ImageUrlMapper;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.*;
import vn.dangthehao.hotel_booking_management.repository.HotelRepository;
import vn.dangthehao.hotel_booking_management.repository.ReviewRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomInventoryRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ApiResponseBuilder;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class HotelService {
  HotelMapper hotelMapper;
  HotelRepository hotelRepository;
  UserService userService;
  MailService mailService;
  UploadFileService uploadFileService;
  ImageUrlMapper imageUrlMapper;
  RoomTypeRepository roomTypeRepository;
  RoomInventoryRepository roomInventoryRepository;
  ReviewRepository reviewRepository;
  RoomTypeMapper roomTypeMapper;

  @NonFinal
  @Value("${file.hotel-img-folder}")
  String hotelImgFolderName;

  public Hotel getByIdWithOwner(Long id) {
    return hotelRepository
        .findByIdFetchOwner(id)
        .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
  }

  public Hotel getConfigurableHotel(Long id) {
    Hotel hotel =
        hotelRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

    if (!HotelStatus.canConfigure(hotel.getStatus()))
      throw new AppException(ErrorCode.HOTEL_NOT_CONFIGURABLE, hotel.getId());

    return hotel;
  }

  public Hotel getActiveHotel(Long id) {
    return hotelRepository
        .findByIdAndStatusAndDeletedFalse(id, HotelStatus.ACTIVE)
        .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
  }

  public boolean isDepositRequired(Hotel hotel) {
    return hotel.getDepositRate() > 0;
  }

  public HotelRegistrationResponse register(
      HotelRegistrationRequest request, MultipartFile thumbnailFile, Long ownerId) {
    Hotel registeredHotel = buildHotelEntity(request, thumbnailFile, ownerId);
    Hotel savedHotel = hotelRepository.save(registeredHotel);

    return buildRegistrationResponse(savedHotel);
  }

  public AdminHotelListResponse getHotelsByStatusForAdmin(HotelStatus status, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<AdminHotelListItemDTO> hotelListItemPage =
        hotelRepository.findHotelsForAdminByStatus(pageable, status);

    List<AdminHotelListItemDTO> hotelList = hotelListItemPage.getContent();
    hotelList.forEach(
        item -> {
          item.setThumbnail(imageUrlMapper.toUrl(item.getThumbnail(), hotelImgFolderName));
          item.setStatus(status);
        });

    return AdminHotelListResponse.builder()
        .hotelList(hotelList)
        .currentPage(page)
        .totalPages(hotelListItemPage.getTotalPages())
        .build();
  }

  public AdminDetailHotelResponse getDetail(Long id) {
    Hotel hotel = getByIdWithOwner(id);

    AdminDetailHotelResponse response = hotelMapper.toDetailHotelResponse(hotel);
    User owner = hotel.getOwner();

    response.setStatus(hotel.getStatus());
    response.setThumbnail(imageUrlMapper.toUrl(response.getThumbnail(), hotelImgFolderName));

    response.setOwnerFullName(owner.getFullName());
    response.setOwnerEmail(owner.getEmail());
    response.setOwnerPhone(owner.getPhone());
    response.setOwnerAvatar(userService.getAvatarUrl(owner));

    return response;
  }

  public void approveHotel(Long id) {
    Hotel hotel = getByIdWithOwner(id);

    if (HotelStatus.PENDING != hotel.getStatus())
      throw new AppException(ErrorCode.HOTEL_ALREADY_PROCESS);

    hotel.setStatus(HotelStatus.INACTIVE);
    hotelRepository.save(hotel);

    String ownerEmail = hotel.getOwner().getEmail();
    mailService.sendApprovalResultAsync(ownerEmail, hotel.getHotelName(), true);
  }

  public void rejectHotel(Long id) {
    Hotel hotel = getByIdWithOwner(id);

    if (HotelStatus.PENDING != hotel.getStatus())
      throw new AppException(ErrorCode.HOTEL_ALREADY_PROCESS);

    hotel.setStatus(HotelStatus.REJECTED);
    hotelRepository.save(hotel);

    String ownerEmail = hotel.getOwner().getEmail();
    mailService.sendApprovalResultAsync(ownerEmail, hotel.getHotelName(), false);
  }

  public OwnerHotelListResponse getHotelsByStatusForOwner(
      Long ownerId, HotelStatus status, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<OwnerHotelListItemDTO> hotelListPage =
        hotelRepository.findHotelsForOwnerByOwnerIdAndStatus(pageable, ownerId, status);

    List<OwnerHotelListItemDTO> hotelList = hotelListPage.getContent();
    hotelList.forEach(
        item -> {
          item.setThumbnail(imageUrlMapper.toUrl(item.getThumbnail(), hotelImgFolderName));
          item.setStatus(status);
        });

    return OwnerHotelListResponse.builder()
        .hotelList(hotelList)
        .currentPage(page)
        .totalPages(hotelListPage.getTotalPages())
        .build();
  }

  public boolean isOwner(Long hotelId, Long ownerId) {
    Long actualOwnerId = getByIdWithOwner(hotelId).getOwner().getId();
    return actualOwnerId.equals(ownerId);
  }

  public ApiResponse<SearchHotelResponse> searchHotels(
      SearchHotelRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Hotel> hotelPageByLocation =
        hotelRepository.findByLocationAndDeletedFalse(request.getLocation(), pageable);
    List<Hotel> hotelsByLocation = hotelPageByLocation.getContent();

    List<HotelInSearchResult> hotelInSearchResultList =
        buildSearchHotelResultList(hotelsByLocation, request);

    SearchHotelResponse response =
        buildSearchHotelResponse(request, hotelPageByLocation, hotelInSearchResultList);

    return ApiResponseBuilder.success("List of hotels found", response);
  }

  private Hotel buildHotelEntity(
      HotelRegistrationRequest request, MultipartFile thumbnailFile, Long ownerId) {
    Hotel registeredHotel = hotelMapper.toHotel(request);

    registeredHotel.setOwner(userService.getByIdWithRole(ownerId));

    if (validateThumbnailFile(thumbnailFile)) {
      String thumbnailUrl = uploadFileService.saveFile(hotelImgFolderName, thumbnailFile);
      String thumbnail = imageUrlMapper.toImageName(thumbnailUrl, hotelImgFolderName);
      registeredHotel.setThumbnail(thumbnail);
    }

    return registeredHotel;
  }

  private boolean validateThumbnailFile(MultipartFile thumbnailFile) {
    return thumbnailFile != null
        && !thumbnailFile.isEmpty()
        && StringUtils.hasText(thumbnailFile.getOriginalFilename());
  }

  private HotelRegistrationResponse buildRegistrationResponse(Hotel hotel) {
    HotelRegistrationResponse response = hotelMapper.toHotelRegistrationResponse(hotel);
    response.setThumbnail(imageUrlMapper.toUrl(hotel.getThumbnail(), this.hotelImgFolderName));
    response.setRoomTypes(Collections.emptyList());

    return response;
  }

  private List<RoomType> findAvailableRoomTypes(List<Hotel> hotels, SearchHotelRequest request) {
    LocalDate checkIn = request.getCheckIn();
    LocalDate checkOut = request.getCheckOut();
    long numOfDays = ChronoUnit.DAYS.between(checkIn, checkOut);

    List<RoomType> roomTypes =
        roomTypeRepository.findByHotelIdInAndCapacityGreaterThanEqual(
            hotels.stream().map(Hotel::getId).toList(), request.getNumGuests());
    Map<Long, List<RoomInventory>> inventoriesByRoomTypeId =
        getInventoriesGroupedByRoomTypeId(roomTypes, checkIn, checkOut);

    return roomTypes.stream()
        .filter(
            rt -> {
              List<RoomInventory> inventories = inventoriesByRoomTypeId.get(rt.getId());

              return inventories != null
                  && inventories.size() == numOfDays
                  && inventories.stream()
                      .allMatch(ri -> ri.getAvailableRooms() >= request.getNumRooms());
            })
        .toList();
  }

  private Map<Long, List<RoomInventory>> getInventoriesGroupedByRoomTypeId(
      List<RoomType> roomTypes, LocalDate checkIn, LocalDate checkOut) {
    if (roomTypes.isEmpty()) return Collections.emptyMap();

    List<Long> roomTypeIds = roomTypes.stream().map(RoomType::getId).toList();

    return roomInventoryRepository
        .findByRoomTypeIdsAndDateRange(roomTypeIds, checkIn, checkOut)
        .stream()
        .collect(Collectors.groupingBy(ri -> ri.getRoomType().getId()));
  }

  private Map<Long, RoomType> getCheapestRoomTypePerHotel(List<RoomType> roomTypes) {
    return roomTypes.stream()
        .collect(
            Collectors.toMap(
                RoomType::getIdOfHotel,
                Function.identity(),
                (rt1, rt2) ->
                    rt1.getPricePerNight().compareTo(rt2.getPricePerNight()) < 0 ? rt1 : rt2));
  }

  private SearchHotelResponse buildSearchHotelResponse(
      SearchHotelRequest request, Page<Hotel> hotelPage, List<HotelInSearchResult> searchResults) {
    return SearchHotelResponse.builder()
        .checkInDate(request.getCheckIn())
        .checkOutDate(request.getCheckOut())
        .currentPage(hotelPage.getNumber() + 1)
        .location(request.getLocation())
        .requiredGuests(request.getNumGuests())
        .requiredRooms(request.getNumRooms())
        .totalPages(hotelPage.getTotalPages())
        .hotels(searchResults)
        .build();
  }

  private List<HotelInSearchResult> buildSearchHotelResultList(
      List<Hotel> hotels, SearchHotelRequest request) {
    List<RoomType> availableRoomTypes = findAvailableRoomTypes(hotels, request);
    Map<Long, RoomType> cheapestRoomTypePerHotel = getCheapestRoomTypePerHotel(availableRoomTypes);

    List<HotelInSearchResult> searchHotelResultList = new ArrayList<>();
    Set<Hotel> suitableHotels =
        availableRoomTypes.stream().map(RoomType::getHotel).collect(Collectors.toSet());
    suitableHotels.forEach(
        hotel -> {
          LowestPriceRoomType lowestPriceRoomType =
              buildLowestPriceRoomType(cheapestRoomTypePerHotel.get(hotel.getId()));

          HotelInSearchResult hotelInSearchResult =
              buildHotelInSearchResult(hotel, lowestPriceRoomType);

          searchHotelResultList.add(hotelInSearchResult);
        });

    return searchHotelResultList;
  }

  private LowestPriceRoomType buildLowestPriceRoomType(RoomType roomType) {
    List<String> amenityNames = roomType.getAmenities().stream().map(Amenity::getName).toList();
    LowestPriceRoomType lowestPriceRoomType = roomTypeMapper.toLowestPriceRoomType(roomType);
    lowestPriceRoomType.setRoomTypeId(roomType.getId());
    lowestPriceRoomType.setAmenityNames(amenityNames);

    return lowestPriceRoomType;
  }

  private HotelInSearchResult buildHotelInSearchResult(
      Hotel hotel, LowestPriceRoomType lowestPriceRoomType) {
    HotelInSearchResult hotelInSearchResult = hotelMapper.toHotelInSearchResult(hotel);
    String thumbnail = hotel.getThumbnail();
    String thumbnailUrl =
        StringUtils.hasText(thumbnail) ? imageUrlMapper.toUrl(thumbnail, hotelImgFolderName) : "";
    hotelInSearchResult.setThumbnailUrl(thumbnailUrl);
    hotelInSearchResult.setTotalReviewCount(reviewRepository.countByHotelId(hotel.getId()));
    hotelInSearchResult.setLowestPriceRoomType(lowestPriceRoomType);

    return hotelInSearchResult;
  }
}
