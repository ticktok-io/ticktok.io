package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
import org.springframework.stereotype.Component;

@Component
public class ResumeClockAction extends RepositoryClockAction {
    private final TickChannelOperations tickChannelOperations;

    public ResumeClockAction(ClocksRepository clocksRepository, TickChannelOperations tickChannelOperations) {
        super(clocksRepository);
        this.tickChannelOperations = tickChannelOperations;
    }

    @Override
    protected void runOnClock(Clock clock) {
        tickChannelOperations.enable(clock);
    }

}
