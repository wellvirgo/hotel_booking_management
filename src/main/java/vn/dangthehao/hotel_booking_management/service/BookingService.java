package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ApiResponse;
import vn.dangthehao.hotel_booking_management.dto.response.BookingResponse;
import vn.dangthehao.hotel_booking_management.enums.BookingPaymentStatus;
import vn.dangthehao.hotel_booking_management.enums.BookingStatus;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.locking.BookingLockStrategy;
import vn.dangthehao.hotel_booking_management.locking.LockStrategyFactory;
import vn.dangthehao.hotel_booking_management.mapper.BookingMapper;
import vn.dangthehao.hotel_booking_management.model.*;
import vn.dangthehao.hotel_booking_management.repository.BookingRepository;
import vn.dangthehao.hotel_booking_management.util.BookingCodeGenerator;
import vn.dangthehao.hotel_booking_management.util.JwtUtil;
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
  AuthenticationService authService;
  PaymentService paymentService;
  BookingRoomService bookingRoomService;
  BookingRepository bookingRepository;
  JwtUtil jwtUtil;
  ResponseGenerator responseGenerator;
  BookingMapper bookingMapper;
  TransactionTemplate transactionTemplate;

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
              // Create a temporal booking
              Booking booking = createBooking(hotel, roomType, bookingRequest);
              // Map room with booking
              bookingRoomService.createBookingRoom(bookingRequest, booking);

              return booking;
            });

    return buildBookingResponse(bookingRequest, savedBooking, hotel);
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
    if (authService.isLoggedIn()) {
      User user = userService.findByID(jwtUtil.getUserID(authService.getJwt()));
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
    return roomType.getPricePerNight().multiply(BigDecimal.valueOf(nights));
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

  private ApiResponse<BookingResponse> buildBookingResponse(
      BookingRequest bookingRequest, Booking booking, Hotel hotel) {
    PaymentRequest paymentRequest =
        PaymentRequest.builder().booking(booking).clientIp(bookingRequest.getClientIp()).build();

    BookingResponse bookingResponse = bookingMapper.toBookingResponse(booking);

    boolean depositRequired = hotelService.isDepositRequired(hotel);
    String depositPaymentUrl =
        depositRequired ? paymentService.createDepositPaymentUrl(paymentRequest) : null;
    bookingResponse.setDepositRequired(depositRequired);
    bookingResponse.setDepositPaymentUrl(depositPaymentUrl);

    // Message for api booking response
    String message = depositRequired ? DEPOSIT_REQUIREMENT_MESS : NOT_DEPOSIT_REQUIREMENT_MESS;

    return responseGenerator.generateSuccessResponse(message, bookingResponse);
  }
}
