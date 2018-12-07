package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.SchedulesPurger;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SchedulesPurgerTest {

    private final SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);

    @Test
    void deleteAllSchedulesWithNoClocks() {
        new SchedulesPurger(schedulesRepository).purge();
        verify(schedulesRepository).deleteNonActiveClocks();
    }

}