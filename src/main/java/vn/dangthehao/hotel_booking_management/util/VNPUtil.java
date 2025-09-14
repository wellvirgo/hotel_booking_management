package vn.dangthehao.hotel_booking_management.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.dto.VNPParamsDTO;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;
import vn.dangthehao.hotel_booking_management.exception.AppException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPUtil {
  static final long DEFAULT_PAYMENT_LINK_VALID_MINUTES = 15;

  public static String buildPaymentUrl(VNPParamsDTO params) {
    Map<String, String> vnpParams = new HashMap<>();
    vnpParams.put("vnp_Version", params.getVersion());
    vnpParams.put("vnp_Command", params.getCommand());
    vnpParams.put("vnp_TmnCode", params.getTmnCode());
    vnpParams.put("vnp_Amount", params.getAmount());
    vnpParams.put("vnp_CurrCode", params.getCurrCode());
    vnpParams.put("vnp_IpAddr", params.getClientIp());
    vnpParams.put("vnp_Locale", params.getLocale());
    vnpParams.put("vnp_TxnRef", params.getTxnRef());
    vnpParams.put("vnp_OrderInfo", params.getOrderInfo());
    vnpParams.put("vnp_OrderType", params.getOrderType());
    vnpParams.put("vnp_ReturnUrl", params.getReturnUrl());
    vnpParams.put("vnp_ExpireDate", params.getExpireDate());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    String vnpCreateDate = sdf.format(cld.getTime());
    vnpParams.put("vnp_CreateDate", vnpCreateDate);

    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator<String> iterator = sortFieldNames(vnpParams);
    while (iterator.hasNext()) {
      String fieldName = iterator.next();
      String fieldValue = vnpParams.get(fieldName);

      // build hash data
      hashData
          .append(fieldName)
          .append("=")
          .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

      // build query
      query
          .append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
          .append("=")
          .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

      if (iterator.hasNext()) {
        hashData.append("&");
        query.append("&");
      }
    }

    query
        .append("&vnp_SecureHash=")
        .append(hmacSHA512(hashData.toString(), params.getHashSecret()));
    String queryUrl = query.toString();

    return params.getPayUrl() + "?" + queryUrl;
  }

  public static String formatAmount(BigDecimal amount) {
    BigDecimal amount100x = amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);

    return String.valueOf(amount100x);
  }

  public static String generateExpTime(Long deadLineMinutes) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    if (deadLineMinutes < DEFAULT_PAYMENT_LINK_VALID_MINUTES) {
      long paymentLinkValidMinutes = deadLineMinutes / 2;
      LocalDateTime expTime = LocalDateTime.now().plusMinutes(paymentLinkValidMinutes);
      return expTime.format(formatter);
    }

    LocalDateTime defaultExpTime =
        LocalDateTime.now().plusMinutes(DEFAULT_PAYMENT_LINK_VALID_MINUTES);
    return defaultExpTime.format(formatter);
  }

  public static String hmacSHA512(String data, String key) {
    Mac hmac512;
    try {
      hmac512 = Mac.getInstance("HmacSHA512");
    } catch (NoSuchAlgorithmException e) {
      throw new AppException(ErrorCode.UNSUPPORTED_HASH_ALGORITHM, "HmacSHA512");
    }

    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
    try {
      hmac512.init(secretKey);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
    StringBuilder hash = new StringBuilder();
    for (byte b : bytes) {
      hash.append(String.format("%02x", b));
    }

    return hash.toString();
  }

  public static boolean validateCheckSum(Map<String, String> params, String hashSecret) {
    String vnpSecureHash = params.remove("vnp_SecureHash");
    params.remove("vnp_SecureHashType");

    StringBuilder hashData = new StringBuilder();
    Iterator<String> sortedFieldNamesIterator = sortFieldNames(params);
    while (sortedFieldNamesIterator.hasNext()) {
      String fieldName = sortedFieldNamesIterator.next();
      String fieldValue = params.get(fieldName);

      if (fieldValue != null && !fieldValue.isBlank()) {
        hashData
            .append(fieldName)
            .append("=")
            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
      }

      if (sortedFieldNamesIterator.hasNext()) {
        hashData.append("&");
      }
    }

    return hmacSHA512(hashData.toString(), hashSecret).equals(vnpSecureHash);
  }

  private static Iterator<String> sortFieldNames(Map<String, String> params) {
    List<String> fieldNames = new ArrayList<>(params.keySet());
    Collections.sort(fieldNames);
    return fieldNames.iterator();
  }
}
