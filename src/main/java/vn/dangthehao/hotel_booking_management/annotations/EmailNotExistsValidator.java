package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.hotel_booking_management.repository.UserRepository;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailNotExistsValidator implements ConstraintValidator<EmailNotExists, String> {
  UserRepository userRepository;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
    return !userRepository.existsByEmailAndIsDeletedFalse(email);
  }
}
