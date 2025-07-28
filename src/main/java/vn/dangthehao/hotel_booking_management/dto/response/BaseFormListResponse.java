package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseFormListResponse {
  int currentPage;
  int totalPages;
}
