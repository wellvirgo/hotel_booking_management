package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorMessage {
    M_INVALID_USERNAME("Username is at least {min} character"),
    M_INVALID_PASSWORD("Password must be at least {min} character " +
            "and include uppercase, lowercase, digit, special character"),
    M_INVALID_FULLNAME("Full name must not blank"),
    M_INVALID_EMAIL("Email is invalid"),
    M_INVALID_PHONE("Phone is invalid"),
    M_USER_NOT_FOUND("User not found"),
    M_WRONG_PASSWORD("Password is not matched"),
    M_USERNAME_IS_EXISTED("Username is existed"),
    M_UNAUTHENTICATED("Unauthenticated"),
    M_UNAUTHORIZED("Have not required permission"),
    M_ROLE_NOT_FOUND("Role not found"),
    M_UNCATEGORIZED("Server has errors");

    String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
