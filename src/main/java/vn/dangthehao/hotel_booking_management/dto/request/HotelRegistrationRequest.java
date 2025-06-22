package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelRegistrationRequest {
    @NotBlank(message = "INVALID_HOTEL_NAME")
    String hotelName;

    @NotBlank(message = "INVALID_ADDRESS")
    String address;
    String description;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1")
    Float depositRate;

    @DecimalMin(value = "0", inclusive = false)
    Float depositDeadlineHours;
}
