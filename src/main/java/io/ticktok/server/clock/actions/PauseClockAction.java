package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import org.springframework.stereotype.Component;

@Component
public class PauseClockAction extends RepositoryClockAction {
    private final TickChannelExplorer tickChannelExplorer;

    public PauseClockAction(ClocksRepository clocksRepository, TickChannelExplorer tickChannelExplorer) {
        super(clocksRepository);
        this.tickChannelExplorer = tickChannelExplorer;
    }

    @Override
    protected void runOnClock(Clock clock) {
        tickChannelExplorer.disable(clock);
    }
}
