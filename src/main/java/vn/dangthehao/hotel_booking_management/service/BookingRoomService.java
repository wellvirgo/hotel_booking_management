package vn.dangthehao.hotel_booking_management.service;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
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
    for (Room room : availableRooms) {
      BookingRoom bookingRoom =
          BookingRoom.builder()
              .booking(booking)
              .room(room)
              .note(String.format(NOTE_PATTERN, booking.getBookingCode()))
              .build();
      bookingRoomRepository.save(bookingRoom);
    }
  }
}
