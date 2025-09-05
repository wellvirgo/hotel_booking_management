package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {GuestInfoValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGuestInfo {
  String message() default "Guest information is invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
