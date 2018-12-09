package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;

import java.util.List;

public class ClocksFinder {

    private final ClocksRepository clocksRepository;

    public ClocksFinder(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }

    public List<Clock> find() {
        return clocksRepository.findByStatusNot(Clock.PENDING);
    }
}
