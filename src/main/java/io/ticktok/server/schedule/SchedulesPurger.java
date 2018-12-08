package io.ticktok.server.schedule;

import io.ticktok.server.schedule.repository.SchedulesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Deleting any unused schedules to speed up repository
 */
@Slf4j
@Component
public class SchedulesPurger {

    private final SchedulesRepository schedulesRepository;

    public SchedulesPurger(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @Scheduled(fixedDelayString = "${purge.schedule:3600000}")
    public void purge() {
        log.info("Purging non active schedules");
        schedulesRepository.deleteNonActiveClocks();
    }
}
