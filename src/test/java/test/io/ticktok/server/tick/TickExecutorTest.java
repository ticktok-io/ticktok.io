package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickExecutor;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.TicksRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class TickExecutorTest {

    private static final long NOW = 1111L;
    TicksRepository ticksRepository = mock(TicksRepository.class);
    TickPublisher tickPublisher = mock(TickPublisher.class);

    @Test
    void publishAllPendingTicks() {
        Tick[] ticks = new Tick[]{
                Tick.create(new Clock("1", "every.5.seconds"), 1000L),
                Tick.create(new Clock("2", "every.4.seconds"), 2000L)
        };
        stubPendingTicks(ticks);
        execute();
        Arrays.stream(ticks).forEach(t -> verify(tickPublisher).publish(t.getClock().getSchedule()));
    }

    private void stubPendingTicks(Tick... ticks) {
        when(ticksRepository.findByStatusAndTimeLessThanEqual(Tick.PENDING, NOW)).thenReturn(Arrays.asList(ticks));
    }

    private void execute() {
        new FixedTimeTickExecutor(ticksRepository, tickPublisher).execute();
    }

    @Test
    void updateInProgressTickStatus() {
        Tick tick = new Tick("id", new Clock("1", "every.5.seconds"), 1000L, "PENDING");
        stubPendingTicks(tick);
        execute();
        InOrder inOrder = Mockito.inOrder(tickPublisher, ticksRepository);
        inOrder.verify(ticksRepository).updateTickStatus(tick.getId(), Tick.IN_PROGRESS);
        inOrder.verify(tickPublisher).publish(any());
    }

    @Test
    void updatePublishedTickStatus() {
        Tick tick = new Tick("id", new Clock("1", "every.5.seconds"), 1000L, "PENDING");
        stubPendingTicks(tick);
        execute();
        InOrder inOrder = Mockito.inOrder(tickPublisher, ticksRepository);
        inOrder.verify(tickPublisher).publish(any());
        inOrder.verify(ticksRepository).updateTickStatus(tick.getId(), Tick.PUBLISHED);
    }

    class FixedTimeTickExecutor extends TickExecutor {


        public FixedTimeTickExecutor(TicksRepository ticksRepository, TickPublisher tickPublisher) {
            super(ticksRepository, tickPublisher);
        }

        @Override
        protected long now() {
            return NOW;
        }
    }
}