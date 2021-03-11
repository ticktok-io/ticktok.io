package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TickChannelCreator {
    private final TickChannelOperations channelExplorer;

    public ChannelConnectionInfo createFor(Clock clock) {
        ChannelConnectionInfo channelConnectionInfo = channelExplorer.create(clock);
        enableClockIfNeeded(clock);
        return channelConnectionInfo;
    }

    private void enableClockIfNeeded(Clock clock) {
        if(!clock.getStatus().equals(Clock.PAUSED)) {
            channelExplorer.enable(clock);
        }
    }
}
