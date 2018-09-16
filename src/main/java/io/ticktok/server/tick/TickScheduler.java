package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;

import java.util.List;

public class TickScheduler {

    public static final int SECOND = 1000;
    public static final int BUFFER = 10 * SECOND;

    private final ClocksRepository clocksRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(ClocksRepository clocksRepository, TicksRepository ticksRepository) {
        this.clocksRepository = clocksRepository;
        this.ticksRepository = ticksRepository;
    }

    public void schedule() {
        List<Clock> clocks = clocksRepository.findByLatestScheduledTickLessThanEqual(now() + BUFFER);
        clocks.forEach(clock -> {
            long nextTickTime = clock.nextTick();
            ticksRepository.save(Tick.create(clock, nextTickTime));
            clocksRepository.updateLatestScheduledTick(clock.getId(), nextTickTime);
        });
    }

    protected long now() {
        return System.currentTimeMillis();
    }

}
