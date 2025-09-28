package vn.dangthehao.hotel_booking_management.util;

import java.util.Random;

public class OTPUtils {
  public static String generateOTP() {
    Random rand = new Random();
    int otp = rand.nextInt(999999);
    return String.format("%06d", otp);
  }
}
