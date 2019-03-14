package test.io.ticktok.server.schedule;

import io.ticktok.server.schedule.ScheduleParser;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
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

    @Test
    void retrieveIntervalForEveryXHoursInSeconds() {
        assertThat(new ScheduleParser("every.2.hours").interval(), is(toSeconds(2, HOURS)));
    }

    private int toSeconds(int amount, TimeUnit unit) {
        return (int) TimeUnit.SECONDS.convert(amount, unit);
    }

    @Test
    void failOnZeroInterval() {
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser("every.0.hours").interval());
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser("every.000.hours").interval());
    }

    @Test
    void failOnNonExactExpression() {
        String expression = "every.1.hours";
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser(expression + "lalala").interval());
        assertThrows(ScheduleParser.ExpressionNotValidException.class, () ->
                new ScheduleParser("www." + expression).interval());
    }

    @Test
    void retrieveIntervalForEveryXMinutes() {
        assertThat(new ScheduleParser("every.4.minutes").interval(), is(toSeconds(4, MINUTES)));
    }

}