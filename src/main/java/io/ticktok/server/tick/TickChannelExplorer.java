package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public interface TickChannelExplorer {
    boolean isExists(Clock clock);
}
