package vn.dangthehao.hotel_booking_management.service;

import java.math.BigDecimal;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.model.Booking;
import vn.dangthehao.hotel_booking_management.model.Payment;

public interface PaymentService {
  Payment createPayment(Booking booking, BigDecimal amount);

  String createDepositPaymentUrl(PaymentRequest paymentRequest);
}
