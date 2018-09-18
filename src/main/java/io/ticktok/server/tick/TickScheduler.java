package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.repository.TicksRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TickScheduler {

    public static final int SECOND = 1000;
    public static final int LOOK_AHEAD = 5 * SECOND;

    private final ClocksRepository clocksRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(ClocksRepository clocksRepository, TicksRepository ticksRepository) {
        this.clocksRepository = clocksRepository;
        this.ticksRepository = ticksRepository;
    }

    @Scheduled(fixedRate = 2500)
    public void schedule() {
        List<Clock> clocks = clocksRepository.findByLatestScheduledTickLessThanEqual(now() + LOOK_AHEAD);
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
