package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;

import java.util.List;

public class TickScheduler {

    private final ClocksRepository clocksRepository;

    public TickScheduler(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }


    // 1. get next tick
    // 2. add it to ticks
    // 3. save it to clocks
    public void schedule() {
        List<Clock> clocks = clocksRepository.findAll();
        clocks.forEach(clock -> {
            clocksRepository.updateLatestScheduledTick(clock.getId(), clock.nextTick());
        });
    }

}
