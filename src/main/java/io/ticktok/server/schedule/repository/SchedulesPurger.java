package io.ticktok.server.schedule.repository;

import io.ticktok.server.logging.LogExecutionTime;
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

    @LogExecutionTime
    @Scheduled(fixedDelayString = "${schedules.purge.interval}")
    public void purge() {
        schedulesRepository.deleteNonActiveSchedules();
    }
}
