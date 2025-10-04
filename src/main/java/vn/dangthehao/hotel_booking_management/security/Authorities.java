package vn.dangthehao.hotel_booking_management.security;

public final class Authorities {
  // ROLE
  public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
  public static final String ADMIN = "ADMIN";
  public static final String HOTEL_OWNER = "HOTEL_OWNER";
  public static final String CUSTOMER = "CUSTOMER";

  // USER PERMISSIONS
  public static final String USER_ALL = "user:*";
  public static final String USER_READ = "user:read";
  public static final String USER_CREATE = "user:create";
  public static final String USER_UPDATE = "user:update";
  public static final String USER_DELETE = "user:delete";

  // HOTEL PERMISSIONS
  public static final String HOTEL_ALL = "hotel:*";
  public static final String HOTEL_READ = "hotel:read";
  public static final String HOTEL_CREATE = "hotel:create";
  public static final String HOTEL_UPDATE = "hotel:update";
  public static final String HOTEL_DELETE = "hotel:delete";

  // BOOKING PERMISSIONS
  public static final String BOOKING_ALL = "booking:*";
  public static final String BOOKING_READ = "booking:read";
  public static final String BOOKING_CREATE = "booking:create";
  public static final String BOOKING_UPDATE = "booking:update";
  public static final String BOOKING_CANCEL = "booking:cancel";

  public static boolean canRegister(String roleName) {
    return CUSTOMER.equalsIgnoreCase(roleName) || HOTEL_OWNER.equalsIgnoreCase(roleName);
  }
}
