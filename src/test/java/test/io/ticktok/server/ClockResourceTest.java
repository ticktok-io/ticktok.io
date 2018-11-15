package test.io.ticktok.server;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClockResource;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ClockResourceTest {

    public static final Clock CLOCK = new Clock("id", "kuku-clock", "at.schedule");

    private final ClockResource resource = new ClockResource("http://kuku", CLOCK);

    @Test
    void delegateClock() {
        assertThat(resource.getId(), is(CLOCK.getId()));
        assertThat(resource.getSchedules(), is(CLOCK.getSchedules()));
    }

    @Test
    void retrieveResourceUrl() {
        assertThat(resource.getUrl(), CoreMatchers.startsWith("http://kuku"));
    }

    @Test
    void retrieveClockName() {
        assertThat(resource.getName(), is(CLOCK.getName()));
    }
}