package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
        return mongo.findAndModify(
                Query.query(Criteria.where("name").is(name).and("schedule").is(schedule)),
                new Update().set("lastModifiedDate", systemClock.millis()).set("status", Clock.PENDING),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
    }

    @Override
    public void deleteClock(Clock clock) {
        log.info("Going to delete: {}" + clock);
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


}
