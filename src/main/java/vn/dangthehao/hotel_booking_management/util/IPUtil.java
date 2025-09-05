package vn.dangthehao.hotel_booking_management.util;

import jakarta.servlet.http.HttpServletRequest;

public class IPUtil {
  public static String getClientIP(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
      return ip.split(",")[0];
    }

    ip = request.getHeader("X-Real-IP");
    if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
      return ip;
    }

    return request.getRemoteAddr();
  }
}
