package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ScheduleCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Slf4j
public class ClocksRepositoryImpl implements UpdateClockRepository {

    private final MongoOperations mongo;
    private final java.time.Clock systemClock;

    public ClocksRepositoryImpl(MongoOperations mongo,
                                java.time.Clock systemClock) {
        this.mongo = mongo;
        this.systemClock = systemClock;
    }

    @Override
    public Clock saveClock(String name, String schedule) {
        Clock clock = mongo.findAndModify(
                Query.query(Criteria.where("name").is(name).and("schedule").is(schedule)),
                new Update().set("lastModifiedDate", systemClock.millis()).set("status", Clock.PENDING),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
        return clock;
    }

    @Override
    public void deleteClock(Clock clock) {
        mongo.remove(
                Query.query(Criteria.where("name").is(clock.getName())
                        .and("schedule").is(clock.getSchedule())
                        .and("lastModifiedDate").is(clock.getLastModifiedDate())),
                Clock.class);

    }

    @Override
    public void updateStatus(String id, String status) {
        mongo.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                Update.update("status", status),
                Clock.class);
    }

    @Override
    public List<ScheduleCount> findByScheduleCount() {
        Aggregation agg = newAggregation(
                Aggregation.group("schedule").count().as("count"),
                project("count").andExpression("_id").as("schedule"));

        return mongo.aggregate(agg, Clock.class, ScheduleCount.class).getMappedResults();
    }
}
