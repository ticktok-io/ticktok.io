package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.time.Clock;
import java.util.List;

@Slf4j
public class SchedulesRepositoryImpl implements CustomSchedulesRepository {

    private static final String NEXT_TICK = "nextTick";
    public static final String CLOCKS = "clocks";
    public static final String SCHEDULE = "schedule";

    private final MongoOperations mongo;
    private final Clock systemTime;

    public SchedulesRepositoryImpl(MongoOperations mongoOperations, Clock systemTime) {
        this.mongo = mongoOperations;
        this.systemTime = systemTime;
    }


    @Override
    @Retryable(value = {DuplicateKeyException.class}, maxAttempts = 2, backoff = @Backoff(delay = 50))
    public void addClock(io.ticktok.server.clock.Clock clock) {
        mongo.findAndModify(
                Query.query(Criteria.where(SCHEDULE).is(clock.getSchedule())),
                new Update()
                        .setOnInsert(NEXT_TICK, systemTime.millis())
                        .addToSet(CLOCKS, clock.getId()),
                FindAndModifyOptions.options().upsert(true),
                Schedule.class);
    }

    @Override
    public void removeClock(io.ticktok.server.clock.Clock clock) {
        mongo.updateFirst(Query.query(Criteria.where(SCHEDULE).is(clock.getSchedule())),
                new Update().pull(CLOCKS, clock.getId()),
                Schedule.class);
    }

    @Override
    public void updateNextTick(String id, long nextTick) {
        mongo.updateFirst(Query.query(Criteria.where("id").is(id)),
                Update.update(NEXT_TICK, nextTick),
                Schedule.class);
    }

    @Override
    public List<Schedule> findActiveSchedulesByNextTickLesserThan(long time) {
        return mongo.find(
                Query.query(Criteria.where(NEXT_TICK).lte(time)
                        .and(CLOCKS).exists(true).not().size(0)),
                Schedule.class);
    }

    @Override
    public void deleteNonActiveClocks() {
        mongo.remove(Query.query(new Criteria().orOperator(
                Criteria.where(CLOCKS).exists(false),
                Criteria.where(CLOCKS).exists(true).size(0))),
                Schedule.class);
    }

}
