package test.io.ticktok.server.schedule;

import io.ticktok.server.schedule.ScheduleParser;
import io.ticktok.server.schedule.ScheduleParser.ExpressionNotValidException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScheduleParserTest {

    @Test
    void shouldFailOnInvalidExpression() {
        assertThatThrownBy(() -> new ScheduleParser("invalid schedule").interval())
                .isInstanceOf(ExpressionNotValidException.class);
    }

    @Test
    void retrieveIntervalForEveryXSecs() {
        assertThat(new ScheduleParser("every.6.seconds").interval()).isEqualTo(6000);
    }

    @Test
    void retrieveIntervalForEveryXHoursInSeconds() {
        assertThat(new ScheduleParser("every.2.hours").interval()).isEqualTo(toMillis(2, HOURS));
    }

    private int toMillis(int amount, TimeUnit unit) {
        return (int) TimeUnit.MILLISECONDS.convert(amount, unit);
    }

    @Test
    void failOnZeroInterval() {
        assertThatThrownBy(() -> new ScheduleParser("every.0.hours").interval())
                .isInstanceOf(ExpressionNotValidException.class);
        assertThatThrownBy(() -> new ScheduleParser("every.000.hours").interval())
                .isInstanceOf(ExpressionNotValidException.class);
    }

    @Test
    void failOnNonExactExpression() {
        String expression = "every.1.hours";
        assertThatThrownBy(() -> new ScheduleParser(expression + "lalala").interval())
                .isInstanceOf(ExpressionNotValidException.class);
        assertThatThrownBy(() -> new ScheduleParser("www." + expression).interval())
                .isInstanceOf(ExpressionNotValidException.class);
    }

    @Test
    void retrieveIntervalForEveryXMinutes() {
        assertThat(new ScheduleParser("every.4.minutes").interval()).isEqualTo(toMillis(4, MINUTES));
    }

    @Test
    void retrieveIntervalForNever() {
        assertThat(new ScheduleParser("@never").interval()).isEqualTo(0);
    }
}