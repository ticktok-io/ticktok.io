package test.io.ticktok.server.clock.schedule;

import io.ticktok.server.clock.schedule.ScheduleValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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