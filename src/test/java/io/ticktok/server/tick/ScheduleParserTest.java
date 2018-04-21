package io.ticktok.server.tick;

import io.ticktok.server.tick.ScheduleParser.ExpressionNotValidException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleParserTest {

    @Test
    void shouldFailOnInvalidExpression() {
        assertThrows(ExpressionNotValidException.class, () ->
                new ScheduleParser("invalid schedule").nextTickTime());
    }

    @Test
    void retrieveTimeForOnceInXSeconds() {
        long sixSecondsFromNow = (System.currentTimeMillis() + 6000) / 1000;
        MatcherAssert.assertThat(new ScheduleParser("once.in.6.seconds").nextTickTime() / 1000, is(sixSecondsFromNow));
    }


}