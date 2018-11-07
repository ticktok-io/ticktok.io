package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Schedule;
import io.ticktok.server.clock.repository.SchedulesRepository;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickScheduler;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class TickSchedulerTest {

    static final long NOW = 1234;

    SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    TicksRepository ticksRepository = mock(TicksRepository.class);

    @Test
    void updateNextTicks() {
        when(schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(anyInt(), anyLong()))
                .thenReturn(asList(
                        createEverySecondsSchedule("2"),
                        createEverySecondsSchedule("4")
                ));
        schedule();
        verify(schedulesRepository).updateLatestScheduledTick("2", 2000L);
        verify(schedulesRepository).updateLatestScheduledTick("4", 4000L);
    }

    private Schedule createEverySecondsSchedule(String schedule) {
        return new Schedule(schedule, "every." + schedule + ".seconds", 0L, 1);
    }

    private void schedule() {
        new FixedTimeTickScheduler(schedulesRepository, ticksRepository).schedule();
    }

    @Test
    void fetchOnlyClocksWithPastScheduledTicks() {
        schedule();
        verify(schedulesRepository).findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(0, NOW + TickScheduler.LOOK_AHEAD);
    }


    @Test
    void shouldNotUpdateClockWhenFailedToScheduleATick() {
        when(schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(anyInt(), anyLong())).thenReturn(asList(
                createEverySecondsSchedule("1")
        ));
        doThrow(RuntimeException.class).when(ticksRepository).save(any());
        try {
            schedule();
        } catch (Throwable e) {
            verify(schedulesRepository, times(0)).updateLatestScheduledTick(eq("1"), anyLong());
        }
    }

    @Test
    void scheduleNewTicks() {
        List<Schedule> clocks = asList(
                createEverySecondsSchedule("2"),
                createEverySecondsSchedule("3")
        );
        when(schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(eq(0), anyLong())).thenReturn(clocks);
        schedule();
        verify(ticksRepository).save(Tick.create(clocks.get(0), 2000L));
        verify(ticksRepository).save(Tick.create(clocks.get(1), 3000L));
    }

    class FixedTimeTickScheduler extends TickScheduler {

        public FixedTimeTickScheduler(SchedulesRepository schedulesRepository, TicksRepository ticksRepository) {
            super(schedulesRepository, ticksRepository);
        }

        @Override
        protected long now() {
            return NOW;
        }
    }


}