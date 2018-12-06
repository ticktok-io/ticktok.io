package io.ticktok.server.schedule;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulesUpdater {


    private final ClocksRepository clocksRepository;
    private final SchedulesRepository schedulesRepository;

    public SchedulesUpdater(ClocksRepository clocksRepository, SchedulesRepository schedulesRepository) {
        this.clocksRepository = clocksRepository;
        this.schedulesRepository = schedulesRepository;
    }

    @Scheduled(fixedDelay = 1000)
    public void update() {
        clocksRepository.findByScheduleCount().forEach(schedulesRepository::saveScheduleGroup);
    }
}
