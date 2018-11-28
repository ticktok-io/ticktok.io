package io.ticktok.server.schedule;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ScheduleValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduleConstraint {
    String message() default "Invalid schedule expression";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
