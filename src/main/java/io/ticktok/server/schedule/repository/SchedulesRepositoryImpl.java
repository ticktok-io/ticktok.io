package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Clock;
import java.util.List;

@Slf4j
public class SchedulesRepositoryImpl implements CustomSchedulesRepository {

    private static final String NEXT_TICK = "nextTick";

    private final MongoOperations mongo;
    private final Clock systemTime;

    public SchedulesRepositoryImpl(MongoOperations mongoOperations, Clock systemTime) {
        this.mongo = mongoOperations;
        this.systemTime = systemTime;
    }

    @Override
    public void addClock(io.ticktok.server.clock.Clock clock) {
        mongo.findAndModify(
                Query.query(Criteria.where("schedule").is(clock.getSchedule())),
                new Update()
                        .setOnInsert(NEXT_TICK, systemTime.millis())
                        .addToSet("clocks", clock.getId()),
                FindAndModifyOptions.options().upsert(true),
                Schedule.class);
    }

    @Override
    public void removeClock(io.ticktok.server.clock.Clock clock) {
        mongo.updateFirst(Query.query(Criteria.where("schedule").is(clock.getSchedule())),
                new Update().pull("clocks", clock.getId()),
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
                Query.query(Criteria.where(NEXT_TICK).lte(systemTime.millis()).and("clocks").exists(true).not().size(0)),
                Schedule.class);
    }

    @Override
    public void deleteNonActiveClocks() {
        mongo.remove(Query.query(new Criteria().orOperator(
                Criteria.where("clocks").exists(false),
                Criteria.where("clocks").exists(true).size(0))),
                Schedule.class);
    }

}
