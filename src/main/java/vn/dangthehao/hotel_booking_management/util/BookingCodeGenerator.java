package vn.dangthehao.hotel_booking_management.util;

import java.security.SecureRandom;
import java.util.Random;

public class BookingCodeGenerator {
  private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final Random RANDOM = new SecureRandom();

  public static String generateCode(int length) {
    StringBuilder code = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      code.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
    }

    return code.toString();
  }
}
