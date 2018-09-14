package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickScheduler;
import io.ticktok.server.tick.TicksRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


class TickSchedulerTest {

    private static final long NOW = 1234;
    ClocksRepository clocksRepository = mock(ClocksRepository.class);
    TicksRepository ticksRepository = mock(TicksRepository.class);

    @Test
    void updateNextTicks() {
        when(clocksRepository.findByLatestScheduledTickLessThanEqual(anyLong())).thenReturn(Arrays.asList(
                new Clock("1", "every.2.seconds", 0L),
                new Clock("2", "every.4.seconds", 0L)
        ));
        schedule();
        verify(clocksRepository).updateLatestScheduledTick("1", 2000L);
        verify(clocksRepository).updateLatestScheduledTick("2", 4000L);
    }

    private void schedule() {
        new FixedTimeTickScheduler(clocksRepository, ticksRepository).schedule();
    }

    @Test
    void fetchOnlyClocksWithPastScheduledTicks() {
        schedule();
        verify(clocksRepository).findByLatestScheduledTickLessThanEqual(NOW + TickScheduler.BUFFER);
    }


    @Test
    void shouldNotUpdateClockWhenFailedToScheduleATick() {
        when(clocksRepository.findByLatestScheduledTickLessThanEqual(anyLong())).thenReturn(Arrays.asList(
                new Clock("1", "every.2.seconds", 0L)
        ));
        doThrow(RuntimeException.class).when(ticksRepository).save(any());
        try {
            schedule();
        } catch(Throwable e) {
            verify(clocksRepository, times(0)).updateLatestScheduledTick(eq("1"), anyLong());
        }
    }

    @Test
    void scheduleNewTicks() {
        when(clocksRepository.findByLatestScheduledTickLessThanEqual(anyLong())).thenReturn(Arrays.asList(
                new Clock("1", "every.2.seconds", 0L),
                new Clock("2", "every.3.seconds", 0L)
        ));
        schedule();
        verify(ticksRepository).save(Tick.create("1", 2000L));
        verify(ticksRepository).save(Tick.create("2", 3000L));
    }

    class FixedTimeTickScheduler extends TickScheduler {

        public FixedTimeTickScheduler(ClocksRepository clocksRepository, TicksRepository ticksRepository) {
            super(clocksRepository, ticksRepository);
        }


        @Override
        protected long now() {
            return NOW;
        }
    }

}