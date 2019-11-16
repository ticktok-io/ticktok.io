package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.stereotype.Component;

@Component
public class TickClockAction implements ClockAction {

    private final TickPublisher tickPublisher;

    public TickClockAction(TickPublisher tickPublisher) {
        this.tickPublisher = tickPublisher;
    }

    @Override
    public void run(Clock clock) {
        tickPublisher.publishForClock(clock);
    }

    @Override
    public boolean availableFor(Clock clock) {
        return true;
    }
}
