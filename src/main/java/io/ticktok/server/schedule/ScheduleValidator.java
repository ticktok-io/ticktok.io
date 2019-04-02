package io.ticktok.server.schedule;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScheduleValidator implements ConstraintValidator<ScheduleConstraint, String> {

    @Override
    public void initialize(ScheduleConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }
        return validateExpressionOf(value);
    }

    private boolean validateExpressionOf(String value) {
        try {
            new ScheduleParser(value).interval();
        } catch (ScheduleParser.ExpressionNotValidException e) {
            return false;
        }
        return true;
    }
}
