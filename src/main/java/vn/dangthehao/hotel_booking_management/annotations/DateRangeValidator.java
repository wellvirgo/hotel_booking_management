package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, HasDateRange> {
  @Override
  public boolean isValid(HasDateRange request, ConstraintValidatorContext context) {
    LocalDate checkInDate = request.getCheckIn();
    LocalDate checkOutDate = request.getCheckOut();
    if (checkInDate == null || checkOutDate == null) return true;

    return checkOutDate.isAfter(checkInDate);
  }
}
