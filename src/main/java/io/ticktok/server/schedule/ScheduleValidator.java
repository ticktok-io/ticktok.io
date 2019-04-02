package io.ticktok.server.schedule;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScheduleValidator implements ConstraintValidator<ScheduleConstraint, String> {

    @Override
    public void initialize(ScheduleConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value != null) {
                new ScheduleParser(value).interval();
                return true;
            }
        } catch (ScheduleParser.ExpressionNotValidException e) {
            return false;
        }
        return false;
    }
}
