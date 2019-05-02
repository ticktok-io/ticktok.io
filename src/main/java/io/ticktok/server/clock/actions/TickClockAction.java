package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.stereotype.Component;

@Component
public class TickClockAction extends RepositoryClockAction {

    private final TickPublisher tickPublisher;

    public TickClockAction(ClocksRepository clocksRepository, TickPublisher tickPublisher) {
        super(clocksRepository);
        this.tickPublisher = tickPublisher;
    }

    @Override
    protected void runOnClock(Clock clock) {
        tickPublisher.publishForClock(clock);
    }
}
