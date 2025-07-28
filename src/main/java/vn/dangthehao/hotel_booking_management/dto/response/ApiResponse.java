package vn.dangthehao.hotel_booking_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  String status;
  int code;
  String message;
  T data;
  List<ErrorDetail> errors;
}
