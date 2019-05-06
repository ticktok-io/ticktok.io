package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public interface TickPublisher {

    void publish(String schedule);

    void publishForClock(Clock clock);
}
