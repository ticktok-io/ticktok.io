package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public interface TickChannelFactory {

    TickChannel create(Clock clock);
}
