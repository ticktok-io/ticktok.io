package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickScheduler2 {

    public static final int SECOND = 1000;

    private final TickPublisher tickPublisher;

    public TickScheduler2(TickPublisher tickPublisher) {
        this.tickPublisher = tickPublisher;
    }

    public void scheduleFor(Clock clock) {
        int interval = new ScheduleParser(clock.getSchedule()).interval();
        new Thread(() -> {
            try {
                Thread.sleep(interval * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickPublisher.publish(clock.getSchedule());
            try {
                Thread.sleep(interval * SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickPublisher.publish(clock.getSchedule());

        }).start();
    }

}
