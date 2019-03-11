package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksPurger;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClocksPurgerTest {

    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);
    private final TickChannelOperations tickChannelOperations = mock(TickChannelOperations.class);

    @Test
    void deleteRedundantClockSchedules() {
        Clock kukuClock = new Clock("111", "kuku", "every.2.seconds");
        Clock popoClock = new Clock("222", "popo", "every.4.seconds");
        when(clocksRepository.findByStatus(Clock.ACTIVE)).thenReturn(Arrays.asList(kukuClock, popoClock));
        when(tickChannelOperations.isExists(any())).thenReturn(false);
        when(tickChannelOperations.isExists(kukuClock)).thenReturn(true);
        purge();
        verify(clocksRepository).deleteClock(popoClock);
        verify(clocksRepository, times(0)).deleteClock(kukuClock);
    }

    private void purge() {
        new ClocksPurger(clocksRepository, tickChannelOperations).purge();
    }

}