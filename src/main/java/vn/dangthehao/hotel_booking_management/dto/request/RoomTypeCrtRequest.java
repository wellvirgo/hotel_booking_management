package vn.dangthehao.hotel_booking_management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeCrtRequest {
    @NotNull(message = "INVALID HOTEL ID")
    Long hotelId;

    @NotBlank(message = "INVALID NAME")
    String name;
    String description;

    @Min(value = 0, message = "Price must be greater than 0")
    BigDecimal pricePerNight;

    @NotNull(message = "INVALID CAPACITY")
    @Min(value = 1, message = "Capacity must be greater than 0")
    int capacity;

    @NotNull(message = "INVALID NUM OF BEDS")
    @Min(value = 1, message = "Num of beds must be greater than 0")
    int numOfBeds;
    String bedType;

    @NotNull(message = "INVALID TOTAL ROOMS")
    @Min(value = 0, message = "Capacity must be greater or equal 0")
    int totalRooms;
    Set<String> amenityNames;
}
