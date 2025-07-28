package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserListResponse {
  Long id;
  String username;
  String avatar;
  String roleName;
}
