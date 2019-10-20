package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
import org.springframework.stereotype.Component;

@Component
public class PauseClockAction implements ClockAction {
    private final TickChannelOperations tickChannelOperations;

    public PauseClockAction(TickChannelOperations tickChannelOperations) {
        this.tickChannelOperations = tickChannelOperations;
    }

    public void run(Clock clock) {
        tickChannelOperations.disable(clock);
    }

    @Override
    public boolean availableFor(Clock clock) {
        return !clock.getStatus().equals(Clock.PAUSED);
    }
}
