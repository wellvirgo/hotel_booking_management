package vn.dangthehao.hotel_booking_management.messaging;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.service.BookingService;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class BookingConsumer {
  BookingService bookingService;

  @RabbitListener(queues = {"${rabbitmq.queue.bookingCancel}"})
  public void handleBookingExpiration(String bookingId) {
    log.info("Booking expiration: {}", bookingId);
    bookingService.cancelExpiredBooking(Long.parseLong(bookingId));
  }
}
