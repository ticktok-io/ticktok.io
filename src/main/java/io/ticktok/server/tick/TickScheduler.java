package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickScheduler {

    private final TickPublisher tickPublisher;

    public TickScheduler(TickPublisher tickPublisher) {
        this.tickPublisher = tickPublisher;
    }

    public void scheduleFor(Clock clock) {
        long tickTime = new ScheduleParser(clock.getSchedule()).nextTickTime();
        new Thread(() -> {
            try {
                Thread.sleep(tickTime - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickPublisher.publish(clock.getSchedule());

        }).start();
    }

}
