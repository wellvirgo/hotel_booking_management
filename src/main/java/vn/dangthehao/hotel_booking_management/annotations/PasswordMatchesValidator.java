package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.hotel_booking_management.dto.PasswordConfirm;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, PasswordConfirm> {
  @Override
  public void initialize(PasswordMatches constraintAnnotation) {}

  @Override
  public boolean isValid(PasswordConfirm obj, ConstraintValidatorContext context) {
    return obj.getConfirmPassword() != null && obj.getConfirmPassword().equals(obj.getPassword());
  }
}
