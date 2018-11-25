package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ClocksRepositoryImpl implements UpdateClockRepository {

    public static final String SCHEDULES = "schedules";

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
                new Update().push(SCHEDULES, schedule),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
        schedulesRepository.addClockFor(schedule);
        return clock;
    }

    @Override
    public void deleteScheduleByIndex(String id, int scheduleIndex) {
        mongo.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().unset(SCHEDULES + "." + scheduleIndex),
                Clock.class);
        mongo.updateFirst(Query.query(Criteria.where("id").is(id)),
                new Update().pull(SCHEDULES, null),
                Clock.class);
    }

    @Override
    public void deleteByNoSchedules() {
        mongo.remove(Query.query(Criteria.where(SCHEDULES).size(0)), Clock.class);
    }
}
