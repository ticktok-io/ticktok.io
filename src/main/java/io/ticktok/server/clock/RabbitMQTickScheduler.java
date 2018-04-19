package io.ticktok.server.clock;

import io.ticktok.server.tick.rabbit.TickPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMQTickScheduler implements TickScheduler {
    public static final String CLOCK_QUEUE = "ticktok-queue";

    private final String rabbitUri;
    private final TickPublisher tickPublisher;

    @Autowired
    public RabbitMQTickScheduler(@Value("${rabbit.uri}") String rabbitUri, TickPublisher tickPublisher) {
        this.tickPublisher = tickPublisher;
        this.rabbitUri = rabbitUri;
    }

    @Override
    public ClockChannel scheduleFor(Clock clock) {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickPublisher.publish(clock.getSchedule());

        }).start();
        return new ClockChannel(rabbitUri, clock.getSchedule());
    }

}
