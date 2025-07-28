package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
  @NotBlank(message = "INVALID_FULL_NAME")
  String fullName;

  @Email(message = "INVALID_EMAIL")
  String email;

  @Size(min = 10, message = "INVALID_PHONE")
  String phone;
}
