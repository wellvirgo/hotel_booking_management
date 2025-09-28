package vn.dangthehao.hotel_booking_management.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotSameAsPasswordValidator.class)
public @interface NotSameAsOldPassword {
  String message() default "New password must be different from old password";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
