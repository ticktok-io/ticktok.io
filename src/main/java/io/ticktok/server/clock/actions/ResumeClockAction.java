package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import org.springframework.stereotype.Component;

@Component
public class ResumeClockAction extends RepositoryClockAction {
    private final TickChannelExplorer tickChannelExplorer;

    public ResumeClockAction(ClocksRepository clocksRepository, TickChannelExplorer tickChannelExplorer) {
        super(clocksRepository);
        this.tickChannelExplorer = tickChannelExplorer;
    }

    @Override
    protected void runOnClock(Clock clock) {
        tickChannelExplorer.enable(clock);
    }

}
