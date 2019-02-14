package io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickPublisher;

public class HttpTickPublisher implements TickPublisher {
    private final HttpQueuesRepository queuesRepository;
    private final ClocksRepository clocksRepository;

    public HttpTickPublisher(HttpQueuesRepository queuesRepository, ClocksRepository clocksRepository) {
        this.queuesRepository = queuesRepository;
        this.clocksRepository = clocksRepository;
    }

    @Override
    public void publish(String schedule) {
        queuesRepository.add(schedule);
    }
}
