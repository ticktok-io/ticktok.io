package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public interface TickPublisher {

    void publish(Tick tick);

    void publishForClock(Clock clock);
}
