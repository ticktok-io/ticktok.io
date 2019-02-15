package io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTickPublisher implements TickPublisher {
    private final HttpQueuesRepository queuesRepository;
    private final ClocksRepository clocksRepository;

    public HttpTickPublisher(HttpQueuesRepository queuesRepository, ClocksRepository clocksRepository) {
        this.queuesRepository = queuesRepository;
        this.clocksRepository = clocksRepository;
    }

    @Override
    public void publish(String schedule) {
        //queuesRepository.add(clockIdsFor(schedule), new TickMessage(schedule));
    }

    private List<String> clockIdsFor(String schedule) {
        return clocksRepository.findBySchedule(schedule).stream().map(Clock::getId).collect(Collectors.toList());
    }
}
