package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.ClocksPurger;
import io.ticktok.server.clock.repository.SchedulesRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.rabbit.QueueNameCreator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClocksPurgerTest {

    private final SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);
    private final TickChannelExplorer tickChannelExplorer = mock(TickChannelExplorer.class);

    @Test
    void deleteRedundantClockSchedules() {
        when(clocksRepository.findAll()).thenReturn(Arrays.asList(
                new Clock("111", "kuku", "every.2.seconds", "every.19.seconds"),
                new Clock("222", "popo", "every.4.seconds")
        ));
        when(tickChannelExplorer.isExists(any())).thenReturn(false);
        when(tickChannelExplorer.isExists(new QueueNameCreator("kuku", "every.2.seconds").create())).thenReturn(true);
        purge();
        verify(clocksRepository).deleteScheduleByIndex("111", 1);
        verify(clocksRepository).deleteScheduleByIndex("222", 0);
    }

    private void purge() {
        new ClocksPurger(schedulesRepository, clocksRepository, tickChannelExplorer).purge();
    }

    @Test
    void deleteAllClocksWithNoSchedules() {
        purge();
        verify(clocksRepository).deleteByNoSchedules();
    }

    @Test
    void removeDeletedSchedules() {
        when(clocksRepository.findAll()).thenReturn(Arrays.asList(
                new Clock("111", "kuku", "every.2.seconds", "every.19.seconds")
        ));
        when(tickChannelExplorer.isExists(any())).thenReturn(true);
        when(tickChannelExplorer.isExists(new QueueNameCreator("kuku", "every.19.seconds").create())).thenReturn(false);
        purge();
        verify(schedulesRepository).removeClockFor("every.19.seconds");
    }

    /*@Test
    void removeDeletedSchedulesFromClock() {
        when(clocksRepository.findAll()).thenReturn(Arrays.asList(
                new Clock("111", "kuku", "every.2.seconds")
        ));
        when(tickChannelExplorer.isExists(new QueueNameCreator("kuku", "every.2.seconds").create())).thenReturn(false);
        purge();
        verify(schedulesRepository).
    }*/

}