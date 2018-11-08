package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.repository.SchedulesPurger;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SchedulesPurgerTest {


    private final SchedulesRepository repository = mock(SchedulesRepository.class);

    @Test
    void deleteAllSchedulesWithNoClocks() {
        new SchedulesPurger(repository).purge();
        verify(repository).deleteByClockCount(0);
    }
}