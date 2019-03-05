package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannelCreator;
import io.ticktok.server.tick.TickChannelExplorer;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TickChannelCreatorTest {

    private final TickChannelExplorer channelExplorer = mock(TickChannelExplorer.class);

    @Test
    void shouldNotEnableChannelOnPausedClock() {
        Clock pausedClock = Clock.builder().status(Clock.PAUSED).build();
        new TickChannelCreator(channelExplorer).createFor(pausedClock);
        verify(channelExplorer, times(0)).enable(pausedClock);
    }
}