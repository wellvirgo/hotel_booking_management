package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetOtpRequest {
  @Email(message = "INVALID_EMAIL")
  String email;
}
