package io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;

public class HttpTickPublisher implements TickPublisher {
    private final HttpQueuesRepository queuesRepository;

    public HttpTickPublisher(HttpQueuesRepository queuesRepository) {
        this.queuesRepository = queuesRepository;
    }

    @Override
    public void publish(Tick tick) {
        queuesRepository.push(new TickMessage(tick.getSchedule()));
    }

    @Override
    public void publishForClock(Clock clock) {
        queuesRepository.push(new QueueNameCreator(clock).create(), new TickMessage(clock.getSchedule()));
    }

}
