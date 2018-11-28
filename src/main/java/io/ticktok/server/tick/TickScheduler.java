package io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import io.ticktok.server.tick.repository.TicksRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TickScheduler {

    public static final int SECOND = 1000;
    public static final int LOOK_AHEAD = 5 * SECOND;

    private final SchedulesRepository schedulesRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(SchedulesRepository schedulesRepository, TicksRepository ticksRepository) {
        this.schedulesRepository = schedulesRepository;
        this.ticksRepository = ticksRepository;
    }

    @Scheduled(fixedRate = 2000)
    public void schedule() {
        toBeScheduleClocks().forEach(schedule -> {
            long nextTickTime = schedule.nextTick();
            ticksRepository.save(Tick.create(schedule, nextTickTime));
            schedulesRepository.updateLatestScheduledTick(schedule.getId(), nextTickTime);
        });
    }

    private List<Schedule> toBeScheduleClocks() {
        return schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(0, now() + LOOK_AHEAD);
    }

    protected long now() {
        return System.currentTimeMillis();
    }

}
