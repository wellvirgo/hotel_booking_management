package vn.dangthehao.hotel_booking_management.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorMessage {
    M_USER_NOT_FOUND("User not found"),
    M_WRONG_PASSWORD("Password is incorrect"),
    M_USERNAME_IS_EXISTED("Username is existed"),
    M_EMAIL_IS_EXISTED("Email is existed"),
    M_UNAUTHENTICATED("Unauthenticated"),
    M_UNAUTHORIZED("Access is denied"),
    M_ROLE_NOT_FOUND("Role not found"),
    M_FAILED_UPLOAD_FILE("Failed upload file"),
    M_VALIDATION_FAILED("Validation failed"),
    M_UNCATEGORIZED("Server has errors");

    String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
