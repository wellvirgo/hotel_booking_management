package vn.dangthehao.hotel_booking_management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.dangthehao.hotel_booking_management.dto.Mail;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MailService {
  JavaMailSender mailSender;
  Environment env;

  @Async
  public void sendChangePasswordEmailAsync(String mailTo) {
    Mail mail = generateMail(mailTo, "Change Password", "You have changed your password");
    sendEmail(mail);
  }

  @Async
  public void sendOTPEmailAsync(String mailTo, String otp) {
    Mail mail = generateMail(mailTo, "OTP for reset password", otp);
    sendEmail(mail);
  }

  @Async
  public void sendApproveHotelEmailAsync(String mailTo, String hotelName) {
    Mail mail =
        generateMail(
            mailTo, "Approve Hotel", String.format("Your %s hotel has been approved", hotelName));
    sendEmail(mail);
  }

  @Async
  public void sendRejectHotelEmailAsync(String mailTo, String hotelName) {
    Mail mail =
        generateMail(
            mailTo, "Reject Hotel", String.format("Your %s hotel has been reject", hotelName));
    sendEmail(mail);
  }

  public void sendEmail(Mail mail) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
      mimeMessageHelper.setFrom(mail.getMailFrom());
      mimeMessageHelper.setTo(mail.getMailTo());
      mimeMessageHelper.setSubject(mail.getSubject());
      mimeMessageHelper.setText(mail.getContent());
      mailSender.send(mimeMessageHelper.getMimeMessage());
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private Mail generateMail(String mailTo, String subject, String content) {
    String mailFrom = env.getProperty("spring.mail.username");
    return Mail.builder()
        .mailTo(mailTo)
        .mailFrom(mailFrom)
        .subject(subject)
        .content(content)
        .build();
  }
}
