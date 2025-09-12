package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.dangthehao.hotel_booking_management.config.VNPConfig;
import vn.dangthehao.hotel_booking_management.dto.VNPParamsDTO;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.enums.PaymentMethod;
import vn.dangthehao.hotel_booking_management.enums.PaymentRecordStatus;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.Payment;
import vn.dangthehao.hotel_booking_management.repository.PaymentRepository;
import vn.dangthehao.hotel_booking_management.util.VNPUtil;

@Component("vnpPayment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayPaymentService implements PaymentService {
  VNPConfig vnpConfig;
  PaymentRepository paymentRepository;

  @NonFinal
  @Value("${vnp.orderInfoPattern}")
  String orderInfoPattern;

  @Override
  public Payment createPayment(Booking booking, BigDecimal amount) {
    String transactionId = UUID.randomUUID().toString();
    Payment payment =
        Payment.builder()
            .transactionId(transactionId)
            .amount(amount)
            .booking(booking)
            .paymentMethod(PaymentMethod.VNPAY)
            .status(PaymentRecordStatus.PENDING)
            .build();
    return paymentRepository.save(payment);
  }

  @Override
  public String createDepositPaymentUrl(PaymentRequest paymentRequest) {
    Booking booking = paymentRequest.getBooking();
    String clientIp = paymentRequest.getClientIp();
    String amountString = VNPUtil.formatAmount(booking.getDepositAmount());
    String orderInfo = String.format(orderInfoPattern, booking.getBookingCode());
    String expireDate = VNPUtil.formatDate(booking.getDepositDeadline());
    Payment payment = createPayment(booking, booking.getDepositAmount());

    VNPParamsDTO vnpParamsDTO =
        buildVNPParams(clientIp, amountString, expireDate, payment.getTransactionId(), orderInfo);

    return VNPUtil.buildPaymentUrl(vnpParamsDTO);
  }

  private VNPParamsDTO buildVNPParams(
      String clientIp, String amount, String expireDate, String txnRef, String orderInfo) {
    return VNPParamsDTO.builder()
        .version(vnpConfig.getVersion())
        .command(vnpConfig.getCommand())
        .tmnCode(vnpConfig.getTmnCode())
        .hashSecret(vnpConfig.getHashSecret())
        .currCode(vnpConfig.getCurrCode())
        .locale(vnpConfig.getLocale())
        .orderType(vnpConfig.getOrderType())
        .returnUrl(vnpConfig.getReturnUrl())
        .clientIp(clientIp)
        .amount(amount)
        .expireDate(expireDate)
        .txnRef(txnRef)
        .orderInfo(orderInfo)
        .payUrl(vnpConfig.getPayUrl())
        .build();
  }
}
