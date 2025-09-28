package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest {
  @NotBlank(message = "Refresh token must not be blank")
  String refreshToken;
}
