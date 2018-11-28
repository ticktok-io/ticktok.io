package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksPurger;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClocksPurgerTest {

    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);
    private final TickChannelExplorer tickChannelExplorer = mock(TickChannelExplorer.class);

    @Test
    void deleteRedundantClockSchedules() {
        Clock kukuClock = new Clock("111", "kuku", "every.2.seconds");
        Clock popoClock = new Clock("222", "popo", "every.4.seconds");
        when(clocksRepository.findByStatus(Clock.ACTIVE)).thenReturn(Arrays.asList(kukuClock, popoClock));
        when(tickChannelExplorer.isExists(any())).thenReturn(false);
        when(tickChannelExplorer.isExists(kukuClock)).thenReturn(true);
        purge();
        verify(clocksRepository).deleteClock(popoClock);
        verify(clocksRepository, times(0)).deleteClock(kukuClock);
    }

    private void purge() {
        new ClocksPurger(clocksRepository, tickChannelExplorer).purge();
    }

}