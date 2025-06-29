package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.annotations.EmailNotExists;
import vn.dangthehao.hotel_booking_management.annotations.PasswordMatches;
import vn.dangthehao.hotel_booking_management.annotations.PasswordStrength;
import vn.dangthehao.hotel_booking_management.annotations.UsernameNotExists;
import vn.dangthehao.hotel_booking_management.dto.PasswordConfirm;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PasswordMatches(message = "PASSWORD_NOT_MATCH")
public class UserCrtRequest implements PasswordConfirm {
    @Size(min = 5, message = "INVALID_USERNAME")
    @UsernameNotExists
    String username;

    @PasswordStrength
    String password;
    String confirmPassword;

    @NotBlank(message = "INVALID_FULL_NAME")
    String fullName;

    @Email(message = "INVALID_EMAIL")
    @EmailNotExists
    String email;

    @Size(min = 10, message = "INVALID_PHONE")
    String phone;
    Long roleId;
}
