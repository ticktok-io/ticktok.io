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
    public static final int LOOK_AHEAD = 5 * SECOND;

    private final long lookaheadInMillis;
    private final SchedulesRepository schedulesRepository;
    private final TicksRepository ticksRepository;

    public TickScheduler(
            @Value("${schedule.lookahead:5}") String lookaheadInSeconds,
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
        Schedule s = schedule;
        while (s.getNextTick() < scheduleTimeFrame) {
            ticksRepository.save(Tick.create(s));
            s = s.nextTick();
        }
        schedulesRepository.updateNextTick(s.getId(), s.getNextTick());
    }

    private List<Schedule> toBeExecutedSchedules() {
        return schedulesRepository.findActiveSchedulesByNextTickLesserThan(now() + LOOK_AHEAD);
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
