package vn.dangthehao.hotel_booking_management.security;

public final class Authorities {
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_HOTEL_OWNER = "ROLE_HOTEL_OWNER";
  public static final String ROLE_USER = "ROLE_USER";

  public static final String ALL_USER = "all:user";
  public static final String READ_USER = "read:user";
  public static final String CREATE_USER = "create:user";
  public static final String UPDATE_USER = "update:user";
  public static final String DELETE_USER = "delete:user";
  public static final String LIST_USER = "list:user";
}
