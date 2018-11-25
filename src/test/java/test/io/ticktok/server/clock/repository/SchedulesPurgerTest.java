package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.schedule.SchedulesPurger;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SchedulesPurgerTest {

    private final SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);

    @Test
    void deleteAllSchedulesWithNoClocks() {
        new SchedulesPurger(schedulesRepository).purge();
        verify(schedulesRepository).deleteByClockCount(0);
    }

}