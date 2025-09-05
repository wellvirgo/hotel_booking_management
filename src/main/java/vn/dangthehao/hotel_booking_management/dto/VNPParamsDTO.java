package vn.dangthehao.hotel_booking_management.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class VNPParamsDTO {
  String version;
  String command;
  String tmnCode;
  String hashSecret;
  String payUrl;
  String currCode;
  String locale;
  String orderType;
  String returnUrl;
  String clientIp;
  String expireDate;
  String bookingId;
  String orderInfo;
  String amount;
}
