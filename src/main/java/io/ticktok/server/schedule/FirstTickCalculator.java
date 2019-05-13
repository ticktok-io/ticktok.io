package io.ticktok.server.schedule;

import io.ticktok.server.clock.Clock;

public class FirstTickCalculator {
    private final java.time.Clock systemClock;

    public FirstTickCalculator(java.time.Clock systemClock) {
        this.systemClock = systemClock;
    }

    public long calcFor(Clock clock) {
        if(new ScheduleParser(clock.getSchedule()).interval() == 0) {
            return Long.MAX_VALUE;
        }
        return systemClock.millis();
    }
}
