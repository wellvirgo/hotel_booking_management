package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.hotel_booking_management.dto.request.ChangePasswordRequest;

public class NotSameAsPasswordValidator
    implements ConstraintValidator<NotSameAsOldPassword, ChangePasswordRequest> {

  @Override
  public boolean isValid(ChangePasswordRequest value, ConstraintValidatorContext context) {
    return !value.getPassword().equals(value.getOldPassword());
  }
}
