package test.io.ticktok.server.schedule;

import io.ticktok.server.schedule.ScheduleValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleValidatorTest {

    @Test
    void failValidationForNonValidSchedule() {
        assertFalse(new ScheduleValidator().isValid("non-valid-schedule", null));
    }

    @Test
    void passGivenValidSchedule() {
        assertTrue(new ScheduleValidator().isValid("every.1.seconds", null));
    }
}