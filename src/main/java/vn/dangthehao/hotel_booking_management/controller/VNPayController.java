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
import vn.dangthehao.hotel_booking_management.dto.response.ResponseForVNP;
import vn.dangthehao.hotel_booking_management.service.VNPayPaymentService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1/vnpay")
public class VNPayController {
  VNPayPaymentService vnpService;

  @GetMapping("/IPN")
  public ResponseEntity<ResponseForVNP> handleIPN(@RequestParam Map<String, String> params) {
    return ResponseEntity.ok(vnpService.handleIPN(params));
  }
}
