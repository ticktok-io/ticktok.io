package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public interface TickChannelCreator {

    TickChannel create(String name, String schedule);
}
