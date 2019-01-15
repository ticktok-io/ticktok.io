package test.io.ticktok.server.clock;

import io.ticktok.server.clock.Clock;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClockTest {

    public static final Clock CLOCK =
            new Clock("11", "kuku", "every.333.seconds", Clock.PENDING, 1L);

    @Test
    void shouldCopyStatus() {
        assertThat(new Clock(CLOCK).getStatus()).isEqualTo(CLOCK.getStatus());
    }

    @Test
    void shouldCopyLastModifiedDate() {
        assertThat(new Clock(CLOCK).getLastModifiedDate()).isEqualTo(CLOCK.getLastModifiedDate());
    }
}