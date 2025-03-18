package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.hotel_booking_management.dto.request.UserCrtRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserCrtRequest userCrtRequest = (UserCrtRequest) obj;
        return userCrtRequest.getPassword() != null
                && userCrtRequest.getPassword().equals(userCrtRequest.getConfirmPassword());
    }
}
