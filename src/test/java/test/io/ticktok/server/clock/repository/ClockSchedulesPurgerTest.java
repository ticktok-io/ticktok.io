package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.ClockSchedulesPurger;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClockSchedulesPurgerTest {

    private final SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);

    @Test
    void purgeOldClockSchedule() {
        when(clocksRepository.findByMoreThanOneSchedule()).thenReturn(Arrays.asList(
                new Clock("111", "kuku", "every.2.seconds", "every.1.seconds", "every.19.seconds"),
                new Clock("222", "popo", "every.4.seconds", "every.5.seconds")
        ));
        purgeClockOldSchedules();
        verify(schedulesRepository).removeClockFor("every.2.seconds", "every.1.seconds");
        verify(schedulesRepository).removeClockFor("every.4.seconds");
    }

    private void purgeClockOldSchedules() {
        new ClockSchedulesPurger(schedulesRepository, clocksRepository).purge();
    }

    @Test
    void removeDeletedSchedulesFromClock() {
        when(clocksRepository.findByMoreThanOneSchedule()).thenReturn(Arrays.asList(
                new Clock("111", "kuku", "every.2.seconds", "every.1.seconds", "every.19.seconds")
        ));
        purgeClockOldSchedules();
        verify(clocksRepository).deleteSchedules("111", Arrays.asList("every.2.seconds", "every.1.seconds"));
    }

    @Test
    void name() {
    }
}