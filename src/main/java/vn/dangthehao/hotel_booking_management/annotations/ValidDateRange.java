package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {DateRangeValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
  String message() default "Check-out date must be after check-in date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
