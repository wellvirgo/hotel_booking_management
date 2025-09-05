package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.config.VNPConfig;
import vn.dangthehao.hotel_booking_management.dto.VNPParamsDTO;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.util.VNPUtil;

@Component("vnpPayment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayPaymentService implements PaymentService {
  VNPConfig vnpConfig;

  @NonFinal
  @Value("${vnp.orderInfoPattern}")
  String orderInfoPattern;

  @Override
  public String createPaymentUrl(PaymentRequest paymentRequest) {
    Booking booking = paymentRequest.getBooking();
    if (booking.getDepositAmount().compareTo(BigDecimal.ZERO) > 0
        && booking.getDepositDeadline() != null) {
      BigDecimal depositAmount100x =
          booking
              .getDepositAmount()
              .multiply(new BigDecimal(100))
              .setScale(0, RoundingMode.HALF_UP);
      String amountString = String.valueOf(depositAmount100x);
      String orderInfo = String.format(orderInfoPattern, booking.getBookingCode());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
      String expireDate = booking.getDepositDeadline().format(formatter);

      VNPParamsDTO vnpParamsDTO =
          VNPParamsDTO.builder()
              .version(vnpConfig.getVersion())
              .command(vnpConfig.getCommand())
              .tmnCode(vnpConfig.getTmnCode())
              .hashSecret(vnpConfig.getHashSecret())
              .currCode(vnpConfig.getCurrCode())
              .locale(vnpConfig.getLocale())
              .orderType(vnpConfig.getOrderType())
              .returnUrl(vnpConfig.getReturnUrl())
              .clientIp(paymentRequest.getClientIp())
              .amount(amountString)
              .expireDate(expireDate)
              .bookingId(String.valueOf(booking.getId()))
              .orderInfo(orderInfo)
              .payUrl(vnpConfig.getPayUrl())
              .build();

      return VNPUtil.buildPaymentUrl(vnpParamsDTO);
    }

    return null;
  }
}
