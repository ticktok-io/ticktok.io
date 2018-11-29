package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;

public class QueueNameCreator {
    private final Clock clock;

    public QueueNameCreator(Clock clock) {
        this.clock = clock;
    }

    public String create() {
        return "ticktok-" + clock.getName() + ";" + clock.getSchedule();
    }
}
