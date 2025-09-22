package vn.dangthehao.hotel_booking_management.service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.enums.RoomStatus;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.BookingRoom;
import vn.dangthehao.hotel_booking_management.model.Room;
import vn.dangthehao.hotel_booking_management.repository.BookingRoomRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingRoomService {
  BookingRoomRepository bookingRoomRepository;
  RoomService roomService;

  static String NOTE_PATTERN = "Room is reserved by booking %s";

  public void createBookingRoom(BookingRequest bookingRequest, Booking booking) {

    List<Room> availableRooms = roomService.getAvailableRoomsForBooking(bookingRequest);
    List<BookingRoom> bookingRooms =
        availableRooms.stream().map(roomToBookingRoomMapper(booking)).toList();
    List<Long> roomIds = availableRooms.stream().map(Room::getId).toList();

    bookingRoomRepository.saveAll(bookingRooms);
    roomService.updateRoomStatuses(roomIds, RoomStatus.BOOKED);
  }

  @Transactional
  public void deleteByIds(List<Long> bookingRoomIds) {
    bookingRoomRepository.deleteByIds(bookingRoomIds);
  }

  @Transactional
  public void releaseRoomByBooking(Booking booking) {
    Set<BookingRoom> bookingRooms = booking.getBookingRooms();

    List<Long> bookingRoomIds = bookingRooms.stream().map(BookingRoom::getId).toList();
    deleteByIds(bookingRoomIds);

    List<Long> roomIds = bookingRooms.stream().map(br -> br.getRoom().getId()).toList();
    roomService.updateRoomStatuses(roomIds, RoomStatus.AVAILABLE);
  }

  private Function<Room, BookingRoom> roomToBookingRoomMapper(Booking booking) {
    return room ->
        BookingRoom.builder()
            .booking(booking)
            .room(room)
            .note(String.format(NOTE_PATTERN, booking.getBookingCode()))
            .build();
  }
}
