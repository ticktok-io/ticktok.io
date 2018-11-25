package test.io.ticktok.server.clock.schedule;

import io.ticktok.server.clock.schedule.ScheduleParser;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleParserTest {

    @Test
    void shouldFailOnInvalidExpression() {
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser("invalid schedules").interval());
    }

    @Test
    void retrieveIntervalForEveryXSecs() {
        MatcherAssert.assertThat(new ScheduleParser("every.6.seconds").interval(), is(6));
    }


}