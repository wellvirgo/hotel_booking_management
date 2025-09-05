package vn.dangthehao.hotel_booking_management.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "vnp")
public class VNPConfig {
  String version;
  String command;
  String tmnCode;
  String hashSecret;
  String payUrl;
  String currCode;
  String locale;
  String orderType;
  String returnUrl;
}
