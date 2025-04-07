package vn.dangthehao.hotel_booking_management.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CheckEmailResponse {
    public static final String EXIST_EMAIL="OTP code will be sent to your email";
    public static final String NOT_EXIST_EMAIL="Your email address does not exist";
}
