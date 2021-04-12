package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;

public class QueueName {

    public static final String PREFIX = "ticktok-";

    public static String createNameFor(Clock clock) {
        return PREFIX + clock.getName() + ";" + clock.getSchedule();
    }
}
