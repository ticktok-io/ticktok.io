package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClocksRepositoryListener extends AbstractMongoEventListener<Clock> {

    private final SchedulesRepository schedulesRepository;
    private final ClocksRepository clocksRepository;
    private final java.time.Clock systemClock;

    public ClocksRepositoryListener(SchedulesRepository schedulesRepository, ClocksRepository clocksRepository, java.time.Clock systemClock) {
        this.schedulesRepository = schedulesRepository;
        this.clocksRepository = clocksRepository;
        this.systemClock = systemClock;
    }

    @Override
    public void onAfterSave(AfterSaveEvent<Clock> event) {
        try {
            schedulesRepository.save(
                   Schedule.createFrom(event.getSource(), systemClock.instant().toEpochMilli()));
        } catch (DuplicateKeyException e) {
            // ignore duplicated schedules
        }
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Clock> event) {
        Optional<Clock> clock = clocksRepository.findById(event.getSource().get("_id").toString());
        clock.ifPresent((c) -> {
            if (clocksRepository.countBySchedule(c.getSchedule()) == 1) {
                schedulesRepository.deleteBySchedule(c.getSchedule());
            }
        });
    }
}
