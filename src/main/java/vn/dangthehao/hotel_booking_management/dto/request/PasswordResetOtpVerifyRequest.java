package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetOtpVerifyRequest {
  @NotNull(message = "Email must not be null")
  String email;

  @NotNull(message = "OTP must not be null")
  String otp;
}
