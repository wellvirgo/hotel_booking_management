package vn.dangthehao.hotel_booking_management.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class BookingCodeGenerator {
  private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final Random RANDOM = new SecureRandom();
  private static final String PREFIX = "BK";

  public static String generateCode(int length) {
    StringBuilder identifier = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      identifier.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
    String date = formatter.format(LocalDate.now());

    return PREFIX + "-" + date + "-" + identifier;
  }
}
