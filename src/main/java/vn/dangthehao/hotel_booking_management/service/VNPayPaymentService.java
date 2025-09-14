package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.hotel_booking_management.config.VNPConfig;
import vn.dangthehao.hotel_booking_management.dto.VNPParamsDTO;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.dto.response.ResponseForVNP;
import vn.dangthehao.hotel_booking_management.enums.*;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.Payment;
import vn.dangthehao.hotel_booking_management.repository.BookingRepository;
import vn.dangthehao.hotel_booking_management.repository.PaymentRepository;
import vn.dangthehao.hotel_booking_management.util.VNPUtil;

@Slf4j
@Component("vnpPayment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayPaymentService implements PaymentService {
  VNPConfig vnpConfig;
  PaymentRepository paymentRepository;
  BookingRepository bookingRepository;

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

  @Transactional
  public ResponseForVNP handleIPN(Map<String, String> params) {
    try {
      if (!VNPUtil.validateCheckSum(params, vnpConfig.getHashSecret())) {
        return buildResForVNP(RspForVNP.INVALID_CHECKSUM);
      }

      Optional<Payment> otpPayment =
          paymentRepository.findByTransactionId(params.get("vnp_TxnRef"));
      if (otpPayment.isEmpty()) {
        return buildResForVNP(RspForVNP.PAYMENT_NOT_FOUND);
      }

      Payment payment = otpPayment.get();
      String expectedAmount = VNPUtil.formatAmount(payment.getAmount());
      String receivedAmount = params.get("vnp_Amount");
      if (!expectedAmount.equals(receivedAmount)) {
        return buildResForVNP(RspForVNP.INVALID_AMOUNT);
      }

      if (payment.getStatus() != PaymentRecordStatus.PENDING) {
        return buildResForVNP(RspForVNP.ALREADY_CONFIRMED);
      }

      String responseCode = params.get("vnp_ResponseCode");
      payment.setGatewayTransactionId(params.get("vnp_TransactionNo"));
      if ("00".equals(responseCode)) {
        payment.setStatus(PaymentRecordStatus.SUCCESS);

        Booking booking = payment.getBooking();
        booking.setPaymentStatus(BookingPaymentStatus.DEPOSIT_PAID);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
      } else {
        payment.setStatus(PaymentRecordStatus.FAILED);
      }
      paymentRepository.save(payment);

      return buildResForVNP(RspForVNP.CONFIRM_SUCCESS);

    } catch (Exception e) {
      log.error("Error handling VNPay IPN", e);
      return buildResForVNP(RspForVNP.UNKNOWN_ERROR);
    }
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

  private ResponseForVNP buildResForVNP(RspForVNP response) {
    return ResponseForVNP.builder()
        .RspCode(response.getRspCode())
        .Message(response.getMessage())
        .build();
  }
}
