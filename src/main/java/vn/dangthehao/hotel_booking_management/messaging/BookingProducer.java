package vn.dangthehao.hotel_booking_management.messaging;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class BookingProducer {
  RabbitTemplate rabbitTemplate;

  @NonFinal
  @Value("${rabbitmq.exchange.booking}")
  String bookingExchange;

  @NonFinal
  @Value("${rabbitmq.routingKey.bookingDelay}")
  String delayRoutingKey;

  public void scheduleBookingCancellation(Long bookingId, LocalDateTime expiration) {
    long delayTime = Duration.between(LocalDateTime.now(), expiration).toMillis();
    rabbitTemplate.convertAndSend(
        bookingExchange,
        delayRoutingKey,
        bookingId,
        message -> {
          message.getMessageProperties().setExpiration(String.valueOf(delayTime));
          return message;
        });
    log.info("Sent booking with id {} to delay queue", bookingId);
  }
}
