package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.annotations.NotSameAsOldPassword;
import vn.dangthehao.hotel_booking_management.annotations.PasswordConfirm;
import vn.dangthehao.hotel_booking_management.annotations.PasswordMatches;
import vn.dangthehao.hotel_booking_management.annotations.PasswordStrength;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatches
@NotSameAsOldPassword
public class ChangePasswordRequest implements PasswordConfirm {
  @NotBlank String oldPassword;

  @PasswordStrength String password;
  String confirmPassword;
}
