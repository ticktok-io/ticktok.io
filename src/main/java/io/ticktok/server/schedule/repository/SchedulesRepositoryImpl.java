package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Clock;

public class SchedulesRepositoryImpl implements UpdateSchedulesRepository {

    private static final String NEXT_TICK = "nextTick";

    private final MongoOperations mongo;
    private final Clock systemTime;

    public SchedulesRepositoryImpl(MongoOperations mongoOperations, Clock systemTime) {
        this.mongo = mongoOperations;
        this.systemTime = systemTime;
    }

    @Override
    public void addSchedule(String schedule) {
        mongo.upsert(
                Query.query(Criteria.where("schedule").is(schedule)),
                new Update().setOnInsert(NEXT_TICK, systemTime.millis()).inc("clockCount", 1),
                Schedule.class);
    }

    @Override
    public void removeSchedule(String schedule) {
        mongo.updateFirst(Query.query(Criteria.where("schedule").is(schedule)),
                new Update().inc("clockCount", -1),
                Schedule.class);
    }
}
