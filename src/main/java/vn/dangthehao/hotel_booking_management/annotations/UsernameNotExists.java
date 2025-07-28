package vn.dangthehao.hotel_booking_management.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UsernameNotExistsValidator.class)
@Documented
public @interface UsernameNotExists {
  String message() default "Username already exists";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
