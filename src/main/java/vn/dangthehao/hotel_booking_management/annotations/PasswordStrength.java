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
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Documented
public @interface PasswordStrength {
  String message() default "Password is weak";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
