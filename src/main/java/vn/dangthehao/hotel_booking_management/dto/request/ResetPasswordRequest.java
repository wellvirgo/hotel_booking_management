package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.annotations.PasswordMatches;
import vn.dangthehao.hotel_booking_management.annotations.PasswordStrength;
import vn.dangthehao.hotel_booking_management.dto.PasswordConfirm;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatches
public class ResetPasswordRequest implements PasswordConfirm {
  @Email(message = "INVALID_EMAIL")
  String email;

  @PasswordStrength String password;
  String confirmPassword;
}
