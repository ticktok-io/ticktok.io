package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public class TickChannelCreator {
    private final TickChannelExplorer channelExplorer;

    public TickChannelCreator(TickChannelExplorer channelExplorer) {
        this.channelExplorer = channelExplorer;
    }

    public TickChannel createFor(Clock clock) {
        TickChannel tickChannel = channelExplorer.create(clock);
        enableClockIfNeeded(clock);
        return tickChannel;
    }

    private void enableClockIfNeeded(Clock clock) {
        if(!clock.getStatus().equals(Clock.PAUSED)) {
            channelExplorer.enable(clock);
        }
    }
}
