package io.ticktok.server.clock.schedule;

import io.ticktok.server.clock.repository.SchedulesRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Deleting any unused schedules to speed up repository
 */
@Component
public class SchedulesPurger {

    private final SchedulesRepository schedulesRepository;

    public SchedulesPurger(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @Scheduled(cron = "0 0 */12 * * *")
    public void purge() {
        schedulesRepository.deleteByClockCount(0);
    }
}
