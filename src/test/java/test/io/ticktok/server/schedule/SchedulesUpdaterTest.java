package test.io.ticktok.server.schedule;

import io.ticktok.server.clock.ScheduleCount;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.schedule.SchedulesUpdater;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchedulesUpdaterTest {


    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);
    private final SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);

    @Test
    void updateSchedulesAccordingToClocks() {
        ScheduleCount scheduleCount11 = new ScheduleCount("every.11.seconds", 2);
        ScheduleCount scheduleCount22 = new ScheduleCount("every.22.seconds", 3);
        when(clocksRepository.findByScheduleCount()).thenReturn(asList(scheduleCount11, scheduleCount22));
        new SchedulesUpdater(clocksRepository, schedulesRepository).update();
        verify(schedulesRepository).saveScheduleGroup(scheduleCount11);
        verify(schedulesRepository).saveScheduleGroup(scheduleCount22);

    }
}