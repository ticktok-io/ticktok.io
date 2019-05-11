package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
public class ClocksRepositoryImpl implements CustomClockRepository {

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
                query(Criteria.where("name").is(name).and("schedule").is(schedule)),
                new Update()
                        .set("lastModifiedDate", systemClock.millis())
                        .setOnInsert("status", Clock.PENDING),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
    }

    @Override
    public void deleteClock(Clock clock) {
        mongo.remove(
                query(Criteria.where("name").is(clock.getName())
                        .and("schedule").is(clock.getSchedule())
                        .and("lastModifiedDate").is(clock.getLastModifiedDate())),
                Clock.class);

    }

    @Override
    public void updateStatus(String id, String status) {
        mongo.updateFirst(
                query(Criteria.where("id").is(id)),
                Update.update("status", status),
                Clock.class);
    }

    @Override
    public List<Clock> findBy(Map<String, String> filter) {
        return mongo.find(query(createCriteriaFrom(filter)), Clock.class);
    }

    private Criteria createCriteriaFrom(Map<String, String> filter) {
        final Criteria criteria = new Criteria();
        filter.forEach((key, value) -> expFor(value, criteria.and(key)));
        return criteria;
    }

    private void expFor(String value, Criteria criteria) {
        if(value.startsWith("!")) {
            criteria.ne(value.substring(1));
        } else {
            criteria.is(value);
        }
    }


}
