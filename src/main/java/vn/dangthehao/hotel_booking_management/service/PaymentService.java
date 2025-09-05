package vn.dangthehao.hotel_booking_management.service;

import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;

public interface PaymentService {
  String createPaymentUrl(PaymentRequest paymentRequest);
}
