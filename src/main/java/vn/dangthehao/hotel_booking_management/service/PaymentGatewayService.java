package vn.dangthehao.hotel_booking_management.service;

import java.util.Map;
import vn.dangthehao.hotel_booking_management.dto.request.PaymentRequest;
import vn.dangthehao.hotel_booking_management.dto.response.PaymentGatewayResponse;

public interface PaymentGatewayService {
  String createDepositPaymentUrl(PaymentRequest paymentRequest);

  PaymentGatewayResponse handleIPN(Map<String, String> params);
}
