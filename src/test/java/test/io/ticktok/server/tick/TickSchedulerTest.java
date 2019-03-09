package test.io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickScheduler;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class TickSchedulerTest {

    static final long NOW = 1000;
    static final int LOOK_AHEAD_IN_SECS = 2;
    static final int LOOK_AHEAD_IN_MILLIS = LOOK_AHEAD_IN_SECS * 1000;

    SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    TicksRepository ticksRepository = mock(TicksRepository.class);

    @Test
    void updateNextTicks() {
        Schedule schedule2 = createEverySecondsSchedule("1");
        Schedule schedule4 = createEverySecondsSchedule("4");
        when(schedulesRepository.findActiveSchedulesByNextTickLesserThan(anyLong()))
                .thenReturn(asList(schedule2, schedule4));
        schedule();

        verify(schedulesRepository).updateNextTick(schedule2.getId(), 3000);
        verify(schedulesRepository).updateNextTick(schedule4.getId(), 5000);
    }

    private Schedule createEverySecondsSchedule(String schedule) {
        return new Schedule(schedule, "every." + schedule + ".seconds", NOW, new ArrayList<>());
    }

    private void schedule() {
        new FixedTimeTickScheduler(schedulesRepository, ticksRepository).schedule();
    }

    @Test
    void fetchOnlyClocksWithPastScheduledTicks() {
        schedule();
        verify(schedulesRepository).findActiveSchedulesByNextTickLesserThan(NOW + LOOK_AHEAD_IN_MILLIS);
    }

    @Test
    void scheduleNewTicks() {
        Schedule schedule1 = createEverySecondsSchedule("3");
        Schedule schedule4 = createEverySecondsSchedule("4");
        when(schedulesRepository.findActiveSchedulesByNextTickLesserThan(anyLong()))
                .thenReturn(asList(schedule1, schedule4));
        schedule();
        verify(ticksRepository).save(Tick.create(schedule1.getSchedule(), NOW));
        verify(ticksRepository).save(Tick.create(schedule4.getSchedule(), NOW));
    }

    @Test
    void scheduleNextTickForNowInCaseTickFromThePast() {
        Schedule schedule = new Schedule("every.4.seconds", NOW - 60 * 1000);
        when(schedulesRepository.findActiveSchedulesByNextTickLesserThan(anyLong())).thenReturn(asList(schedule));
        schedule();
        verify(ticksRepository, only()).save(Tick.create(schedule.getSchedule(), NOW));
    }

    @Test
    void scheduleMultipleTicksUpToLookAhead() {
        Schedule schedule = createEverySecondsSchedule("1");
        when(schedulesRepository.findActiveSchedulesByNextTickLesserThan(anyLong())).thenReturn(asList(schedule));
        schedule();
        verify(ticksRepository).save(Tick.create(schedule.getSchedule(), NOW));
        verify(ticksRepository).save(Tick.create(schedule.getSchedule(), NOW + 1000));

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

}