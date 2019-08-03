package io.ticktok.server.clock.actions;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.RepositoryClocksFinder;
import io.ticktok.server.clock.repository.ClocksRepository;

public abstract class RepositoryClockAction implements ClockAction {

    private final ClocksRepository clocksRepository;

    public RepositoryClockAction(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }

    @Override
    public void run(String id) {
        Clock clock = new RepositoryClocksFinder(clocksRepository).findById(id);
        runOnClock(clock);
    }

    protected abstract void runOnClock(Clock clock);
}
