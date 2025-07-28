package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class ChangePasswordRequest implements PasswordConfirm {
  @NotBlank String oldPassword;

  @PasswordStrength String password;
  String confirmPassword;
}
