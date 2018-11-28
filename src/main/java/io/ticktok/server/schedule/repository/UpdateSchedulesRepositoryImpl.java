package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Clock;

public class UpdateSchedulesRepositoryImpl implements UpdateSchedulesRepository {

    private final MongoOperations mongo;
    private final Clock systemTime;

    public UpdateSchedulesRepositoryImpl(MongoOperations mongoOperations, Clock systemTime) {
        this.mongo = mongoOperations;
        this.systemTime = systemTime;
    }

    @Override
    public void updateLatestScheduledTick(String id, long time) {
        mongo.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                Update.update("latestScheduledTick", time),
                Schedule.class);
    }

    @Override
    public void addSchedule(String schedule) {
        mongo.upsert(
                Query.query(Criteria.where("schedule").is(schedule)),
                new Update().setOnInsert("latestScheduledTick", systemTime.millis()).inc("clockCount", 1),
                Schedule.class);
    }

    @Override
    public void removeSchedule(String schedule) {
        mongo.updateFirst(Query.query(Criteria.where("schedule").is(schedule)),
                new Update().inc("clockCount", -1),
                Schedule.class);
    }
}
