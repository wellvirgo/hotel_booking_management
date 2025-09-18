package vn.dangthehao.hotel_booking_management.controller;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.dangthehao.hotel_booking_management.dto.response.PaymentGatewayResponse;
import vn.dangthehao.hotel_booking_management.service.PaymentGatewayService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
public class PaymentGateWayCallbackController {
  PaymentGatewayService paymentGatewayService;

  @GetMapping("/vnpay/IPN")
  public ResponseEntity<PaymentGatewayResponse> vnpIPN(@RequestParam Map<String, String> params) {
    return ResponseEntity.ok(paymentGatewayService.handleIPN(params));
  }
}
