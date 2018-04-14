package io.ticktok.server.clock;

public interface TickScheduler {

    ClockChannel scheduleFor(Clock clock);
}
