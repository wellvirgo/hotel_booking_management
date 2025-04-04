package vn.dangthehao.hotel_booking_management.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Mail {
    String mailFrom;
    String mailTo;
    String mailCC;
    String mailBCC;
    String subject;
    String content;
    String contentType;
    List<Object> attachments;

    public Date getMailSendDate() {
        return new Date();
    }
}
