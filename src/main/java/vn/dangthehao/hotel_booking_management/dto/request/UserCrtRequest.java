package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCrtRequest {
    @Size(min = 5, message = "INVALID_USERNAME")
    String username;
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,}$",
            message = "INVALID_PASSWORD"
    )
    String password;
    @NotBlank(message = "INVALID_FULLNAME")
    String fullName;
    @Email(message = "INVALID_EMAIL")
    String email;
    @Size(min = 10, message = "INVALID_PHONE")
    String phone;
    Long roleId;
}
