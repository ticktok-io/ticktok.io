package test.io.ticktok.server;

import io.ticktok.server.Clock;
import io.ticktok.server.ClockResource;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class ClockResourceTest {

    public static final Clock CLOCK = new Clock("id", "at.schedule", null);

    private final ClockResource resource = new ClockResource(URI.create("http://kuku"), CLOCK);

    @Test
    void delegateClock() {
        assertThat(resource.getId(), is(CLOCK.getId()));
        assertThat(resource.getSchedule(), is(CLOCK.getSchedule()));
    }

    @Test
    void retrieveResourceUrl() {
        assertThat(resource.getUrl(), is("http://kuku"));
    }
}