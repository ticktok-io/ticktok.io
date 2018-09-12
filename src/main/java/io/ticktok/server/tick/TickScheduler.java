package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;

import java.util.List;

public class TickScheduler {

    private final ClocksRepository clocksRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(ClocksRepository clocksRepository, TicksRepository ticksRepository) {
        this.clocksRepository = clocksRepository;
        this.ticksRepository = ticksRepository;
    }


    // 1. get next tick
    // 2. add it to ticks
    // 3. save it to clocks
    public void schedule() {
        List<Clock> clocks = clocksRepository.findByLatestScheduledTickLessThanEqual(TenSecondsFromNow());
        clocks.forEach(clock -> {
            long nextTickTime = clock.nextTick();
            clocksRepository.updateLatestScheduledTick(clock.getId(), nextTickTime);
            ticksRepository.save(Tick.create(clock.getId(), nextTickTime));
        });
    }

    private long TenSecondsFromNow() {
        return System.currentTimeMillis() + 10 * 1000;
    }

}
