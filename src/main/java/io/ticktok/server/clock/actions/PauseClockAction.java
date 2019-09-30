package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
import org.springframework.stereotype.Component;

@Component
public class PauseClockAction extends RepositoryClockAction {
    private final TickChannelOperations tickChannelOperations;

    public PauseClockAction(ClocksRepository clocksRepository, TickChannelOperations tickChannelOperations) {
        super(clocksRepository);
        this.tickChannelOperations = tickChannelOperations;
    }

    @Override
    protected void runOnClock(Clock clock) {
        tickChannelOperations.disable(clock);
    }

    @Override
    public boolean availableFor(Clock clock) {
        return !clock.getStatus().equals(Clock.PAUSED);
    }
}
