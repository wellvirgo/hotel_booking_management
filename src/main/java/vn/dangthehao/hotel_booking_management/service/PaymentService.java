package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.enums.PaymentMethod;
import vn.dangthehao.hotel_booking_management.enums.PaymentRecordStatus;
import vn.dangthehao.hotel_booking_management.exception.AppException;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.Payment;
import vn.dangthehao.hotel_booking_management.repository.PaymentRepository;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class PaymentService {
  PaymentRepository paymentRepository;

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

  public Payment findById(Long id) {
    return paymentRepository
        .findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "id" + id));
  }

  public Payment updatePayment(Payment payment) {
    return paymentRepository.save(payment);
  }

  public Payment findByTransactionId(String transactionId) {
    return paymentRepository
        .findByTransactionId(transactionId)
        .orElseThrow(
            () -> new AppException(ErrorCode.PAYMENT_NOT_FOUND, "transactionId" + transactionId));
  }
}
