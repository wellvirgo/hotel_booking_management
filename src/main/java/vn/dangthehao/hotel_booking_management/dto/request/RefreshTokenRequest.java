package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {
  @NotBlank(message = "Grant type must be not blank")
  String grantType;

  @NotBlank(message = "Refresh token must be not blank")
  @Size(min = 40, message = "Refresh token is invalid")
  String refreshToken;
}
