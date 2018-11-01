package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickExecutor;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.repository.TicksRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class TickExecutorTest {

    static final long NOW = 1111L;

    TicksRepository ticksRepository = mock(TicksRepository.class);
    TickPublisher tickPublisher = mock(TickPublisher.class);

    @Test
    void publishAllPendingTicks() {
        Tick[] ticks = new Tick[]{
                Tick.create(new Clock("1", "kuku", "every.5.seconds"), 1000L),
                Tick.create(new Clock("2", "kuku", "every.4.seconds"), 2000L)
        };
        executeGivenTick(ticks);
        Arrays.stream(ticks).forEach(t -> verify(tickPublisher).publish(t.getSchedule()));
    }

    private void executeGivenTick(Tick... ticks) {
        stubPendingTicks(ticks);
        new FixedTimeTickExecutor(ticksRepository, tickPublisher).execute();
    }

    private void stubPendingTicks(Tick... ticks) {
        when(ticksRepository.findByStatusAndTimeLessThanEqual(Tick.PENDING, NOW)).thenReturn(Arrays.asList(ticks));
    }

    @Test
    void updateInProgressTickStatus() {
        Tick tick = createPendingTick();
        executeGivenTick(tick);
        InOrder inOrder = Mockito.inOrder(tickPublisher, ticksRepository);
        inOrder.verify(ticksRepository).updateTickStatus(tick.getId(), Tick.IN_PROGRESS);
        inOrder.verify(tickPublisher).publish(any());
    }

    @NotNull
    private Tick createPendingTick() {
        return new Tick("id", "1", "every.5.seconds", 1000L, "PENDING");
    }

    @Test
    void updatePublishedTickStatus() {
        Tick tick = createPendingTick();
        executeGivenTick(tick);
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