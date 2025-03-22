package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.hotel_booking_management.enums.ErrorCode;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    static final String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (!password.matches(passwordRegex)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorCode.WEAK_PASSWORD.getMessage())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
