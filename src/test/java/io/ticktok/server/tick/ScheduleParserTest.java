package io.ticktok.server.tick;

import io.ticktok.server.clock.ScheduleParser;
import io.ticktok.server.clock.ScheduleParser.ExpressionNotValidException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleParserTest {

    @Test
    void shouldFailOnInvalidExpression() {
        assertThrows(ExpressionNotValidException.class, () ->
                new ScheduleParser("invalid schedule").interval());
    }

    @Test
    void retrieveIntervalForEveryXSecs() {
        MatcherAssert.assertThat(new ScheduleParser("every.6.seconds").interval(), is(6));
    }


}