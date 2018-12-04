package test.io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickScheduler;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static test.io.ticktok.server.tick.TickSchedulerTest.NextTickMatcher.nextTickIsForInterval;


class TickSchedulerTest {

    static final long NOW = 1000;
    static final int LOOK_AHEAD_IN_SECS = 2;

    SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    TicksRepository ticksRepository = mock(TicksRepository.class);

    @Test
    void updateNextTicks() {
        Schedule schedule2 = createEverySecondsSchedule("1");
        Schedule schedule4 = createEverySecondsSchedule("4");
        when(schedulesRepository.findByClockCountGreaterThanAndNextTickLessThanEqual(anyInt(), anyLong()))
                .thenReturn(asList(schedule2, schedule4));
        schedule();

        verify(schedulesRepository).updateNextTick(schedule2.getId(), 3000);
        verify(schedulesRepository).updateNextTick(schedule4.getId(), 5000);
    }

    private Schedule createEverySecondsSchedule(String schedule) {
        return new Schedule(schedule, "every." + schedule + ".seconds", NOW, 1);
    }

    private void schedule() {
        new FixedTimeTickScheduler(schedulesRepository, ticksRepository).schedule();
    }

    @Test
    void fetchOnlyClocksWithPastScheduledTicks() {
        schedule();
        verify(schedulesRepository).findByClockCountGreaterThanAndNextTickLessThanEqual(0, NOW + TickScheduler.LOOK_AHEAD);
    }

    @Test
    void scheduleNewTicks() {
        Schedule schedule1 = createEverySecondsSchedule("1");
        Schedule schedule3 = createEverySecondsSchedule("3");
        List<Schedule> schedules = asList(schedule1, schedule3);
        when(schedulesRepository.findByClockCountGreaterThanAndNextTickLessThanEqual(eq(0), anyLong())).thenReturn(schedules);
        schedule();
        verify(ticksRepository).save(Tick.create(schedule1));
        schedule1 = schedule1.nextTick();
        verify(ticksRepository).save(Tick.create(schedule1));
        verify(ticksRepository).save(Tick.create(schedule3));
    }

    static class FixedTimeTickScheduler extends TickScheduler {

        public FixedTimeTickScheduler(SchedulesRepository schedulesRepository, TicksRepository ticksRepository) {
            super(String.valueOf(LOOK_AHEAD_IN_SECS), schedulesRepository, ticksRepository);
        }

        @Override
        protected long now() {
            return NOW;
        }
    }

    static class NextTickMatcher implements ArgumentMatcher<Schedule> {

        private final long expectedNextTick;

        public NextTickMatcher(long expectedNextTick) {
            this.expectedNextTick = expectedNextTick;
        }

        public static NextTickMatcher nextTickIsForInterval(long expectedNextTick) {
            return new NextTickMatcher(expectedNextTick);
        }

        @Override
        public boolean matches(Schedule actualSchedule) {
            return actualSchedule.getNextTick() == expectedNextTick;
        }

    }


}