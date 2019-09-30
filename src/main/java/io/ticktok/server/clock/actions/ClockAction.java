package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;

public interface ClockAction {

    void run(String id);

    boolean availableFor(Clock clock);
}
