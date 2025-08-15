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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.dangthehao.hotel_booking_management.dto.HotelInSearchResult;
import vn.dangthehao.hotel_booking_management.dto.LowestPriceRoomType;
import vn.dangthehao.hotel_booking_management.dto.OwnerHotelItemDTO;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;
import vn.dangthehao.hotel_booking_management.dto.request.HotelRegistrationRequest;
import vn.dangthehao.hotel_booking_management.dto.request.SearchHotelRequest;
import vn.dangthehao.hotel_booking_management.dto.response.*;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.HotelStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.mapper.HotelMapper;
import vn.dangthehao.hotel_booking_management.mapper.RoomTypeMapper;
import vn.dangthehao.hotel_booking_management.model.Amenity;
import vn.dangthehao.hotel_booking_management.model.Hotel;
import vn.dangthehao.hotel_booking_management.model.RoomInventory;
import vn.dangthehao.hotel_booking_management.model.RoomType;
import vn.dangthehao.hotel_booking_management.repository.HotelRepository;
import vn.dangthehao.hotel_booking_management.repository.ReviewRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomInventoryRepository;
import vn.dangthehao.hotel_booking_management.repository.RoomTypeRepository;
import vn.dangthehao.hotel_booking_management.util.ImageNameToUrlMapper;
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
  UploadFileService uploadFileService;
  ImageNameToUrlMapper imageNameToUrlMapper;
  RoomTypeRepository roomTypeRepository;
  RoomInventoryRepository roomInventoryRepository;
  ReviewRepository reviewRepository;
  RoomTypeMapper roomTypeMapper;

  static String BASE_AVATAR_URL = "http://localhost:8080/avatars/";

  @NonFinal
  @Value("${base_url}")
  String baseUrl;

  @NonFinal
  @Value("${file.hotel_img_folder_name}")
  String hotelImgFolderName;

  public Hotel findById(Long id) {
    return hotelRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
  }

  public ApiResponse<HotelRegistrationResponse> register(
      HotelRegistrationRequest request, MultipartFile thumbnailFile, Jwt jwt) {
    Hotel registeredHotel = createHotelFromRequest(request, thumbnailFile, jwt);
    Hotel savedHotel = hotelRepository.save(registeredHotel);
    HotelRegistrationResponse response =
        buildHotelRegistrationResponse(savedHotel, jwtUtil.getUserID(jwt));

    return responseGenerator.generateSuccessResponse(
        "Your application will be handled soon!", response);
  }

  public ApiResponse<UnapprovedHotelsResponse> findUnapprovedHotels(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<UnapprovedHotelDTO> unapprovedHotelDTOPage =
        hotelRepository.findUnapprovedHotels(pageable, HotelStatus.INACTIVE);

    UnapprovedHotelsResponse unapprovedHotelsResponse =
        UnapprovedHotelsResponse.builder()
            .unapprovedHotels(unapprovedHotelDTOPage.getContent())
            .currentPage(page)
            .totalPages(unapprovedHotelDTOPage.getTotalPages())
            .build();
    return responseGenerator.generateSuccessResponse(
        "List of unapproved hotels", unapprovedHotelsResponse);
  }

  public ApiResponse<DetailHotelResponse> getDetailHotel(Long id) {
    Hotel hotel = findById(id);
    DetailHotelResponse response = hotelMapper.toDetailHotelResponse(hotel);
    response.setOwnerFullName(hotel.getOwner().getFullName());
    response.setOwnerEmail(hotel.getOwner().getEmail());
    response.setOwnerPhone(hotel.getOwner().getPhone());
    String avatarUrl =
        (hotel.getOwner().getAvatar() != null)
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

  public ApiResponse<OwnerHotelsResponse> findHotelsByOwner(
      Jwt jwt, String isApproved, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<OwnerHotelItemDTO> ownerHotelItemDTOPage = null;
    OwnerHotelsResponse ownerHotelsResponse = null;
    if ("false".equals(isApproved)) {
      ownerHotelItemDTOPage =
          hotelRepository.findUnApprovedHotelsByOwner(pageable, jwtUtil.getUserID(jwt));
      ownerHotelsResponse =
          OwnerHotelsResponse.builder()
              .ownerHotelItems(ownerHotelItemDTOPage.getContent())
              .currentPage(page)
              .totalPages(ownerHotelItemDTOPage.getTotalPages())
              .build();

      return responseGenerator.generateSuccessResponse(
          "List of owner unapproved hotels", ownerHotelsResponse);
    }

    ownerHotelItemDTOPage =
        hotelRepository.findApprovedHotelsByOwner(pageable, jwtUtil.getUserID(jwt));
    ownerHotelsResponse =
        OwnerHotelsResponse.builder()
            .ownerHotelItems(ownerHotelItemDTOPage.getContent())
            .currentPage(page)
            .totalPages(ownerHotelItemDTOPage.getTotalPages())
            .build();

    return responseGenerator.generateSuccessResponse(
        "List of approved hotels", ownerHotelsResponse);
  }

  public boolean isOwner(Long hotelId, Jwt jwt) {
    Long ownerId = findById(hotelId).getOwner().getId();
    return ownerId.equals(jwtUtil.getUserID(jwt));
  }

  public ApiResponse<SearchHotelResponse> searchHotels(
      SearchHotelRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Hotel> hotelPageByLocation =
        hotelRepository.findByLocationAndIsApprovedTrueAndIsDeletedFalse(
            request.getLocation(), pageable);
    List<Hotel> hotelsByLocation = hotelPageByLocation.getContent();

    List<HotelInSearchResult> hotelInSearchResultList =
        buildSearchHotelResultList(hotelsByLocation, request);

    SearchHotelResponse response =
        buildSearchHotelResponse(request, hotelPageByLocation, hotelInSearchResultList);

    return responseGenerator.generateSuccessResponse("List of hotels found", response);
  }

  private Hotel createHotelFromRequest(
      HotelRegistrationRequest request, MultipartFile thumbnailFile, Jwt jwt) {
    Hotel registeredHotel = hotelMapper.toHotel(request);
    Long ownerId = jwtUtil.getUserID(jwt);
    registeredHotel.setOwner(userService.findByID(ownerId));

    if (validateThumbnailFile(thumbnailFile)) {
      String thumbnail =
          uploadFileService
              .saveFile(hotelImgFolderName, thumbnailFile)
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
    HotelRegistrationResponse response = hotelMapper.toHotelRegistrationResponse(hotel);
    response.setOwnerId(ownerId);
    response.setApproved(hotel.isApproved());
    response.setThumbnailUrl(
        imageNameToUrlMapper.toUrl(hotel.getThumbnail(), this.hotelImgFolderName));
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
        .findByRoomTypeIdAndDateRange(roomTypeIds, checkIn, checkOut.minusDays(1))
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
        StringUtils.hasText(thumbnail)
            ? imageNameToUrlMapper.toUrl(thumbnail, hotelImgFolderName)
            : "";
    hotelInSearchResult.setThumbnailUrl(thumbnailUrl);
    hotelInSearchResult.setTotalReviewCount(reviewRepository.countByHotelId(hotel.getId()));
    hotelInSearchResult.setLowestPriceRoomType(lowestPriceRoomType);

    return hotelInSearchResult;
  }
}
