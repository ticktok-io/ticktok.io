package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.Tick;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

class TickTest {

    @Test
    void defaultStatusShouldBePending() {
        assertThat(Tick.create(new Clock(), 0L).getStatus(), is(Tick.PENDING));
    }
}