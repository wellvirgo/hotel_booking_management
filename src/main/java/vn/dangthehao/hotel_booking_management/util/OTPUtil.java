package vn.dangthehao.hotel_booking_management.util;

import java.util.Random;

public class OTPUtil {
  public static OTPUtil getInstance() {
    return new OTPUtil();
  }

  public static String generateOTP() {
    Random rand = new Random();
    int otp = rand.nextInt(999999);
    return String.format("%06d", otp);
  }
}
