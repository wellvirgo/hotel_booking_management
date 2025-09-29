package vn.dangthehao.hotel_booking_management.service;

import java.util.Map;
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
import vn.dangthehao.hotel_booking_management.dto.response.VNPResponse;
import vn.dangthehao.hotel_booking_management.enums.*;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.Payment;
import vn.dangthehao.hotel_booking_management.repository.BookingRepository;
import vn.dangthehao.hotel_booking_management.util.VNPUtils;

@Slf4j
@Component("vnpPayment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService implements PaymentGatewayService {
  VNPConfig vnpConfig;
  PaymentService paymentService;
  BookingRepository bookingRepository;

  @NonFinal
  @Value("${vnp.orderInfoPattern}")
  String orderInfoPattern;

  @Override
  public String createDepositPaymentUrl(PaymentRequest paymentRequest) {
    Booking booking = paymentRequest.getBooking();
    String clientIp = paymentRequest.getClientIp();
    String amountString = VNPUtils.formatAmount(booking.getDepositAmount());
    String orderInfo = String.format(orderInfoPattern, booking.getBookingCode());
    String expireDate = VNPUtils.generateExpTime(booking.getHotel().getDepositDeadlineMinutes());
    Payment payment = paymentService.createPayment(booking, booking.getDepositAmount());

    VNPParamsDTO vnpParamsDTO =
        buildVNPParams(clientIp, amountString, expireDate, payment.getTransactionId(), orderInfo);

    return VNPUtils.buildPaymentUrl(vnpParamsDTO);
  }

  @Override
  @Transactional
  public VNPResponse handleIPN(Map<String, String> params) {
    try {
      if (!VNPUtils.validateCheckSum(params, vnpConfig.getHashSecret())) {
        return buildResForVNP(RspForVNP.INVALID_CHECKSUM);
      }

      Payment payment = paymentService.findByTransactionId(params.get("vnp_TxnRef"));
      String expectedAmount = VNPUtils.formatAmount(payment.getAmount());
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
      paymentService.updatePayment(payment);

      return buildResForVNP(RspForVNP.CONFIRM_SUCCESS);

    } catch (AppException appEx) {
      if (appEx.getErrorCode() == ErrorCode.PAYMENT_NOT_FOUND) {
        log.error(appEx.getErrorMessage(), appEx);
        return buildResForVNP(RspForVNP.PAYMENT_NOT_FOUND);
      }
      return buildResForVNP(RspForVNP.UNKNOWN_ERROR);
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

  private VNPResponse buildResForVNP(RspForVNP response) {
    return VNPResponse.builder()
        .RspCode(response.getRspCode())
        .Message(response.getMessage())
        .build();
  }
}
