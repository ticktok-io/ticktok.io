package io.ticktok.server.clock;

import io.ticktok.server.tick.TickPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TickScheduler {

    private final TickPublisher tickPublisher;

    public TickScheduler(TickPublisher tickPublisher) {
        this.tickPublisher = tickPublisher;
    }

    public void scheduleFor(Clock clock) {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickPublisher.publish(clock.getSchedule());

        }).start();
    }

}
