package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, PasswordConfirm> {

  @Override
  public boolean isValid(PasswordConfirm obj, ConstraintValidatorContext context) {
    return obj.getConfirmPassword() != null && obj.getConfirmPassword().equals(obj.getPassword());
  }
}
