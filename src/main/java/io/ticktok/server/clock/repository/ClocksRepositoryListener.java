package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClocksRepositoryListener extends AbstractMongoEventListener<Clock> {

    private final SchedulesRepository schedulesRepository;
    private final ClocksRepository clocksRepository;

    public ClocksRepositoryListener(SchedulesRepository schedulesRepository, ClocksRepository clocksRepository) {
        this.schedulesRepository = schedulesRepository;
        this.clocksRepository = clocksRepository;
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Clock> event) {
        String clockId = event.getSource().get("_id").toString();
        Optional<Clock> clock = clocksRepository.findById(clockId);
        clock.ifPresent((c) -> schedulesRepository.removeClockFor(clock.get().getSchedules().toArray(new String[0])));
    }

}
