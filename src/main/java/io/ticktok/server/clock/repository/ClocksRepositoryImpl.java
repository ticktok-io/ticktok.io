package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class ClocksRepositoryImpl implements UpdateClockRepository {

    private final MongoOperations mongo;
    private final SchedulesRepository schedulesRepository;

    public ClocksRepositoryImpl(MongoOperations mongo,
                                SchedulesRepository schedulesRepository) {
        this.mongo = mongo;
        this.schedulesRepository = schedulesRepository;
    }

    @Override
    public Clock saveClock(String name, String schedule) {
        Clock clock = mongo.findAndModify(
                Query.query(Criteria.where("name").is(name)),
                new Update().push("schedules", schedule),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
        schedulesRepository.addClockFor(schedule);
        return clock;
    }

    @Override
    public void deleteSchedules(String id, List<String> schedules) {

    }
}
