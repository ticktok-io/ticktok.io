package test.io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.tick.Tick;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TickTest {

    private final static long NOW = 43245;

    @Test
    void defaultStatusShouldBePending() {
        assertThat(Tick.create(new Schedule()).getStatus()).isEqualTo(Tick.PENDING);
    }

    @Test
    void keepTimeWhenLargerThanBound() {
        assertThat(Tick.create("every.3.seconds", NOW + 1000).boundTo(NOW).getTime()).isEqualTo(NOW + 1000);
    }

    @Test
    void shouldRetrieveTTL() {
        assertThat(Tick.create("every.2.seconds", NOW).ttl()).isEqualTo(2000 - 1);
    }
}