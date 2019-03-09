package test.io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.tick.Tick;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

class TickTest {

    private final static long NOW = 43245;

    @Test
    void defaultStatusShouldBePending() {
        assertThat(Tick.create(new Schedule()).getStatus(), is(Tick.PENDING));
    }

    @Test
    void NotAlteredTickTimeIfItLargerThanBoundTime() {
        assertThat(Tick.create("every.3.seconds", NOW + 1000).boundTo(NOW).getTime(), is(NOW + 1000));
    }
}