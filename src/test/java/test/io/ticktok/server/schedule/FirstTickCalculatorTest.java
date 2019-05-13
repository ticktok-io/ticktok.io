package test.io.ticktok.server.schedule;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.schedule.FirstTickCalculator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class FirstTickCalculatorTest {

    public static final Instant INSTANT = Instant.parse("2019-02-02T10:15:00.00Z");

    private static final java.time.Clock SYSTEM_CLOCK = java.time.Clock.fixed(INSTANT, ZoneId.of("UTC"));
    private FirstTickCalculator calculator = new FirstTickCalculator(SYSTEM_CLOCK);

    @Test
    void retrieveTimeForNever() {
        assertThat(calculator.calcFor(new Clock("kuku", "@never"))).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void ignoreCaseOfNever() {
        assertThat(calculator.calcFor(new Clock("kuku", "@nEveR"))).isEqualTo(Long.MAX_VALUE);
    }
}