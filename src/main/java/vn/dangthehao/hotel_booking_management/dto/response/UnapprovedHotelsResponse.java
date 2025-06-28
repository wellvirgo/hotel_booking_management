package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.hotel_booking_management.dto.BaseFormListResponse;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnapprovedHotelsResponse extends BaseFormListResponse {
    List<UnapprovedHotelDTO> unapprovedHotels;
}
