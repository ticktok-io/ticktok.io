package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Schedule;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class UpdateSchedulesRepositoryImpl implements UpdateSchedulesRepository {

    private final MongoOperations mongo;

    public UpdateSchedulesRepositoryImpl(MongoOperations mongoOperations) {
        this.mongo = mongoOperations;
    }

    @Override
    public void updateLatestScheduledTick(String id, long time) {
        mongo.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                Update.update("latestScheduledTick", time),
                Schedule.class);
    }

    @Override
    public void decreaseClockCount(String schedule) {
        incScheduleClockCountBy(schedule, -1);
    }

    private void incScheduleClockCountBy(String schedule, int amount) {
        mongo.updateFirst(
                Query.query(Criteria.where("schedule").is(schedule)),
                new Update().inc("clockCount", amount),
                Schedule.class);
    }

    @Override
    public void increaseClockCount(String schedule) {
        incScheduleClockCountBy(schedule, 1);
    }

    @Override
    public void saveSchedule(Schedule schedule) {
        mongo.upsert(
                Query.query(Criteria.where("schedule").is(schedule.getSchedule())),
                createUpdateFor(schedule),
                Schedule.class);

    }

    @Override
    public void addClockFor(String schedule) {

    }

    @Override
    public void removeClockFor(String schedule) {

    }

    private Update createUpdateFor(Schedule schedule) {
        return Update.update("latestScheduledTick", schedule.getLatestScheduledTick())
                .set("clockCount", schedule.getClockCount());
    }
}
