package io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import io.ticktok.server.tick.repository.TicksRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TickScheduler {

    public static final int SECOND = 1000;

    private final long lookaheadInMillis;
    private final SchedulesRepository schedulesRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(
            @Value("${schedules.lookahead:5}") String lookaheadInSeconds,
            SchedulesRepository schedulesRepository,
            TicksRepository ticksRepository) {
        this.lookaheadInMillis = Long.valueOf(lookaheadInSeconds) * SECOND;
        this.schedulesRepository = schedulesRepository;
        this.ticksRepository = ticksRepository;
    }

    @Scheduled(fixedRate = 2000)
    public void schedule() {
        long scheduleTimeFrame = now() + lookaheadInMillis;
        toBeExecutedSchedules().forEach(schedule -> scheduleNextTickFor(schedule, scheduleTimeFrame));
    }

    private void scheduleNextTickFor(final Schedule schedule, long scheduleTimeFrame) {
        Tick t = schedule.nextTick().boundTo(now());
        while (t.getTime() < scheduleTimeFrame) {
            ticksRepository.save(t);
            t = t.nextTick();
        }
        schedulesRepository.updateNextTick(schedule.getId(), t.getTime());
    }

    private List<Schedule> toBeExecutedSchedules() {
        return schedulesRepository.findActiveSchedulesByNextTickLesserThan(now() + lookaheadInMillis);
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
