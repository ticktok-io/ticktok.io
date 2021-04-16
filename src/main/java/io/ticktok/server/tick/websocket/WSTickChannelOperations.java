package io.ticktok.server.tick.websocket;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.ChannelConnectionInfo;
import io.ticktok.server.tick.TickChannelOperations;

public class WSTickChannelOperations implements TickChannelOperations {
    @Override
    public boolean isExists(Clock clock) {
        return false;
    }

    @Override
    public ChannelConnectionInfo create(Clock clock) {
        return null;
    }

    @Override
    public void disable(Clock clock) {

    }

    @Override
    public void enable(Clock clock) {

    }
}
