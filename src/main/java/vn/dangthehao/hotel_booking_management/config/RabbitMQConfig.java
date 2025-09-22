package vn.dangthehao.hotel_booking_management.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.bookingDelay}")
  String bookingDelay;

  @Value("${rabbitmq.queue.bookingCancel}")
  String bookingCancel;

  @Value("${rabbitmq.exchange.booking}")
  String bookingExchange;

  @Value("${rabbitmq.routingKey.bookingDelay}")
  String delayRoutingKey;

  @Value("${rabbitmq.routingKey.bookingCancel}")
  String cancelRoutingKey;

  @Bean
  public Queue bookingDelayQueue() {
    return QueueBuilder.durable(bookingDelay)
        .withArgument("x-dead-letter-exchange", bookingExchange)
        .withArgument("x-dead-letter-routing-key", cancelRoutingKey)
        .build();
  }

  @Bean
  public Queue bookingCancelQueue() {
    return new Queue(bookingCancel);
  }

  @Bean
  public DirectExchange bookingExchange() {
    return new DirectExchange(bookingExchange);
  }

  @Bean
  public Binding bookingDelayBinding() {
    return BindingBuilder.bind(bookingDelayQueue()).to(bookingExchange()).with(delayRoutingKey);
  }

  @Bean
  public Binding bookingCancelBinding() {
    return BindingBuilder.bind(bookingCancelQueue()).to(bookingExchange()).with(cancelRoutingKey);
  }
}
