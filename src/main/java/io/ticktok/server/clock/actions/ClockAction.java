package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;

public interface ClockAction {

    void run(Clock clock);

    boolean availableFor(Clock clock);
}
