package vn.dangthehao.hotel_booking_management.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.dangthehao.hotel_booking_management.repository.InvalidTokenRepository;
import vn.dangthehao.hotel_booking_management.service.DataInitializerService;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppInitConfig {
  InvalidTokenRepository invalidTokenRepository;
  DataInitializerService dataInitializerService;

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      invalidTokenRepository.deleteByExpiredTime();
      dataInitializerService.initializeDefaultData();
    };
  }
}
