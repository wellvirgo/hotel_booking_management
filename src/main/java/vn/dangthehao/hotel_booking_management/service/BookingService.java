package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.BookingResponse;
import vn.dangthehao.hotel_booking_management.enums.BookingPaymentStatus;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.PaymentRecordStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.locking.BookingLockStrategy;
import vn.dangthehao.hotel_booking_management.locking.LockStrategyFactory;
import vn.dangthehao.hotel_booking_management.mapper.BookingMapper;
import vn.dangthehao.hotel_booking_management.messaging.BookingProducer;
import vn.dangthehao.hotel_booking_management.model.*;
import vn.dangthehao.hotel_booking_management.repository.BookingRepository;
import vn.dangthehao.hotel_booking_management.security.JwtService;
import vn.dangthehao.hotel_booking_management.security.SecurityUtils;
import vn.dangthehao.hotel_booking_management.util.BookingCodeGenerator;
import vn.dangthehao.hotel_booking_management.util.ResponseGenerator;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BookingService {
  static String DEPOSIT_REQUIREMENT_MESS = "Booking is holding";
  static String NOT_DEPOSIT_REQUIREMENT_MESS = "Booking is confirmed";

  LockStrategyFactory lockStrategyFactory;
  HotelService hotelService;
  RoomTypeService roomTypeService;
  UserService userService;
  PaymentService paymentService;
  PaymentGatewayService paymentGatewayService;
  BookingRoomService bookingRoomService;
  RoomInventoryService roomInventoryService;
  BookingRepository bookingRepository;
  JwtService jwtService;
  ResponseGenerator responseGenerator;
  BookingMapper bookingMapper;
  TransactionTemplate transactionTemplate;
  BookingProducer bookingProducer;

  @NonFinal
  @Value("${booking-code.length}")
  int bookingCodeLength;

  @NonFinal
  @Value("${lock-strategy}")
  String lockStrategyName;

  public ApiResponse<BookingResponse> holdReservation(BookingRequest bookingRequest) {
    Hotel hotel = hotelService.findApprovedHotelById(bookingRequest.getHotelId());
    RoomType roomType = roomTypeService.findActiveRoomTypeById(bookingRequest.getRoomTypeId());

    BookingLockStrategy bookingLockStrategy = lockStrategyFactory.getLockStrategy(lockStrategyName);
    Booking savedBooking =
        transactionTemplate.execute(
            status -> {
              // Lock to avoid overbooking
              lockAndValidateInventory(bookingLockStrategy, bookingRequest);
              // Create a booking
              Booking booking = createBooking(hotel, roomType, bookingRequest);
              // Map room with booking
              bookingRoomService.createBookingRoom(bookingRequest, booking);

              return booking;
            });

    if (savedBooking != null && hotelService.isDepositRequired(hotel)) {
      bookingProducer.scheduleBookingCancellation(
          savedBooking.getId(), savedBooking.getDepositDeadline());
    }
    return buildBookingResponse(bookingRequest, savedBooking, hotel);
  }

  public void cancelExpiredBooking(Long bookingId) {
    Booking booking = getBookingWithRooms(bookingId);
    if (booking.getStatus() == BookingStatus.PENDING) {
      LocalDate checkIn = booking.getCheckIn().toLocalDate();
      LocalDate checkOut = booking.getCheckOut().toLocalDate();
      Long roomTypeId = roomTypeService.getIdByBookingId(bookingId);
      int quantity = booking.getNumRooms();

      transactionTemplate.execute(
          status -> {
            roomInventoryService.increaseRoomInventory(roomTypeId, checkIn, checkOut, quantity);
            bookingRoomService.releaseRoomByBooking(booking);
            paymentService.updatePaymentStatus(bookingId, PaymentRecordStatus.CANCELLED);
            return updateBookingStatus(booking, BookingStatus.EXPIRED);
          });
    }
  }

  public int updateBookingStatus(Booking booking, BookingStatus newStatus) {
    BookingStatus currStatus = booking.getStatus();
    if (!currStatus.canTransitionTo(newStatus)) {
      log.error("Booking status transition is invalid");
      throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION, "Booking", currStatus, newStatus);
    }

    return bookingRepository.updateBookingStatusById(booking.getId(), newStatus);
  }

  public Booking getBookingWithRooms(Long id) {
    return bookingRepository
        .findByIdFetchRooms(id)
        .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND, id));
  }

  private void lockAndValidateInventory(
      BookingLockStrategy bookingLockStrategy, BookingRequest bookingRequest) {
    List<RoomInventory> lockedRoomInventory =
        bookingLockStrategy.lock(
            bookingRequest.getRoomTypeId(),
            bookingRequest.getCheckIn(),
            bookingRequest.getCheckOut());

    // Double check room available before reversing
    for (RoomInventory ri : lockedRoomInventory) {
      if (ri.getAvailableRooms() < bookingRequest.getNumRooms()) {
        throw new AppException(ErrorCode.NOT_ENOUGH_ROOMS, bookingRequest.getRoomTypeId());
      }
      ri.setAvailableRooms(ri.getAvailableRooms() - bookingRequest.getNumRooms());
    }
  }

  private Booking saveBooking(Booking tempBooking) {
    String bookingCode;
    Booking savedBooking = new Booking();
    boolean saved = false;

    while (!saved) {
      bookingCode = BookingCodeGenerator.generateCode(bookingCodeLength);
      tempBooking.setBookingCode(bookingCode);
      try {
        savedBooking = bookingRepository.save(tempBooking);
        saved = true;
      } catch (Exception e) {
        log.error("Duplicate booking code: {}", bookingCode);
      }
    }

    return savedBooking;
  }

  private Booking createBooking(Hotel hotel, RoomType roomType, BookingRequest bookingRequest) {
    LocalDateTime checkIn = bookingRequest.getCheckIn().atTime(hotel.getCheckInTime());
    LocalDateTime checkOut = bookingRequest.getCheckOut().atTime(hotel.getCheckOutTime());

    BigDecimal totalPrice = calculateTotalPrice(roomType, bookingRequest);
    BigDecimal depositAmount = calculateDepositAmount(hotel, totalPrice);
    LocalDateTime depositDeadline = calculateDepositDeadline(hotel);
    BookingStatus status =
        hotelService.isDepositRequired(hotel) ? BookingStatus.PENDING : BookingStatus.CONFIRMED;

    Booking booking =
        Booking.builder()
            .hotel(hotel)
            .roomType(roomType)
            .numRooms(bookingRequest.getNumRooms())
            .checkIn(checkIn)
            .checkOut(checkOut)
            .totalPrice(totalPrice)
            .depositAmount(depositAmount)
            .depositDeadline(depositDeadline)
            .status(status)
            .paymentStatus(BookingPaymentStatus.PENDING)
            .build();
    setGuestInfo(booking, bookingRequest);

    return saveBooking(booking);
  }

  private void setGuestInfo(Booking booking, BookingRequest bookingRequest) {
    // If user log in, will use user account information
    if (SecurityUtils.isLoggedIn()) {
      Long userId = jwtService.getUserId((Jwt) SecurityUtils.getAuthentication().getPrincipal());
      User user = userService.getByIdWithRole(userId);
      booking.setUser(user);
      return;
    }

    // If user doesn't log in, user info in request
    booking.setGuestName(bookingRequest.getGuestName());
    booking.setGuestPhone(bookingRequest.getGuestPhone());
    booking.setGuestEmail(bookingRequest.getGuestEmail());
  }

  private BigDecimal calculateTotalPrice(RoomType roomType, BookingRequest bookingRequest) {
    long nights =
        ChronoUnit.DAYS.between(bookingRequest.getCheckIn(), bookingRequest.getCheckOut());
    return roomType
        .getPricePerNight()
        .multiply(BigDecimal.valueOf(nights))
        .multiply(BigDecimal.valueOf(bookingRequest.getNumRooms()));
  }

  private BigDecimal calculateDepositAmount(Hotel hotel, BigDecimal totalPrice) {
    if (!hotelService.isDepositRequired(hotel)) return BigDecimal.ZERO;
    // Avoid decimal point error when creating BigDecimal from Float
    String depositRate = String.valueOf(hotel.getDepositRate());

    return totalPrice.multiply(new BigDecimal(depositRate));
  }

  private LocalDateTime calculateDepositDeadline(Hotel hotel) {
    if (!hotelService.isDepositRequired(hotel)) return null;
    return LocalDateTime.now().plusMinutes(hotel.getDepositDeadlineMinutes());
  }

  private String createDepositPaymentUrlIfRequired(Booking booking, Hotel hotel, String clientIp) {
    if (!hotelService.isDepositRequired(hotel)) return null;

    var paymentRequest = PaymentRequest.builder().booking(booking).clientIp(clientIp).build();
    return paymentGatewayService.createDepositPaymentUrl(paymentRequest);
  }

  private ApiResponse<BookingResponse> buildBookingResponse(
      BookingRequest bookingRequest, Booking booking, Hotel hotel) {
    String depositPaymentUrl =
        createDepositPaymentUrlIfRequired(booking, hotel, bookingRequest.getClientIp());

    BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);
    boolean depositRequired = depositPaymentUrl != null;
    bookingResponse.setDepositRequired(depositRequired);
    bookingResponse.setDepositPaymentUrl(depositPaymentUrl);

    // Message for api booking response
    String message = depositRequired ? DEPOSIT_REQUIREMENT_MESS : NOT_DEPOSIT_REQUIREMENT_MESS;

    return responseGenerator.generateSuccessResponse(message, bookingResponse);
  }
}
