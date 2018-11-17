package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ClockSchedulesPurger {

    private final SchedulesRepository schedulesRepository;
    private final ClocksRepository clocksRepository;

    public ClockSchedulesPurger(SchedulesRepository schedulesRepository,
                                ClocksRepository clocksRepository) {
        this.schedulesRepository = schedulesRepository;
        this.clocksRepository = clocksRepository;
    }

    @Scheduled(cron = "0 0 */4 * * *")
    public void purge() {
        clocksRepository.findByMoreThanOneSchedule().forEach(c -> {
            List<String> toBeDeleted = schedulesWithoutTheLastOneFrom(c);
            schedulesRepository.removeClockFor(toBeDeleted.toArray(new String[0]));
            clocksRepository.deleteSchedules(c.getId(), toBeDeleted);
        });
    }

    private List<String> schedulesWithoutTheLastOneFrom(Clock c) {
        return c.getSchedules().subList(0, c.getSchedules().size() - 1);
    }

}
