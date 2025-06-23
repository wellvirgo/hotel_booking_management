package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.dto.UnapprovedHotelDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnapprovedHotelListResponse {
    List<UnapprovedHotelDTO> unapprovedHotels;
    int currentPage;
    int totalPages;
}
