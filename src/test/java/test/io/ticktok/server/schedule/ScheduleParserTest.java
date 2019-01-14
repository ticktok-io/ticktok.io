package test.io.ticktok.server.schedule;

import io.ticktok.server.schedule.ScheduleParser;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduleParserTest {

    @Test
    void shouldFailOnInvalidExpression() {
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser("invalid schedule").interval());
    }

    @Test
    void retrieveIntervalForEveryXSecs() {
        assertThat(new ScheduleParser("every.6.seconds").interval(), is(6));
    }


}