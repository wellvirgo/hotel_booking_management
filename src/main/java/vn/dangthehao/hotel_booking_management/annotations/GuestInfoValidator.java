package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.EmailValidator;
import vn.dangthehao.hotel_booking_management.dto.request.BookingRequest;
import vn.dangthehao.hotel_booking_management.service.AuthenticationService;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GuestInfoValidator implements ConstraintValidator<ValidGuestInfo, BookingRequest> {
  AuthenticationService authService;

  @Override
  public boolean isValid(BookingRequest request, ConstraintValidatorContext context) {
    if (!authService.isLoggedIn()) {
      boolean isValidName = request.getGuestName() != null && !request.getGuestName().isBlank();
      boolean isValidEmail =
          request.getGuestEmail() != null
              && !request.getGuestEmail().isBlank()
              && EmailValidator.getInstance().isValid(request.getGuestEmail());
      boolean isValidPhone = request.getGuestPhone() != null && !request.getGuestPhone().isBlank();

      return isValidName && isValidEmail && isValidPhone;
    }
    return true;
  }
}
