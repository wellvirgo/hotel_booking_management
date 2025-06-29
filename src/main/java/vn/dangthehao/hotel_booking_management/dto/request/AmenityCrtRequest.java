package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AmenityCrtRequest {
    @NotBlank(message = "INVALID_AMENITY_NAME")
    String name;
}
