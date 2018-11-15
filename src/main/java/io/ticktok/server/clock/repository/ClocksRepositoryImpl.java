package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

public class ClocksRepositoryImpl implements UpdateClockRepository {

    private final MongoOperations mongo;
    private final SchedulesRepository schedulesRepository;
    private final java.time.Clock systemClock;

    public ClocksRepositoryImpl(MongoOperations mongo,
                                SchedulesRepository schedulesRepository,
                                java.time.Clock systemClock) {
        this.mongo = mongo;
        this.schedulesRepository = schedulesRepository;
        this.systemClock = systemClock;
    }

    @Override
    public Clock saveClock(String name, String schedule) {
        return mongo.findAndModify(
                Query.query(Criteria.where("name").is(name)),
                new Update().push("schedules", schedule),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);


            /*Update update = Update.update("schedule", clock.getSchedules());
            Clock savedClock = mongo.findAndModify(
                    Query.query(Criteria.where("name").is(clock.getName())),
                    update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    Clock.class);
            //schedulesRepository.saveSchedule(Schedule.createFrom(clock, systemClock.millis()));
            return savedClock;*/
    }
}
