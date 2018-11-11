package io.ticktok.server.clock.repository;

import com.mongodb.client.result.UpdateResult;
import io.ticktok.server.clock.Clock;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ClocksRepositoryImpl implements UpdateClockRepository {

    private final MongoOperations mongo;

    public ClocksRepositoryImpl(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public Clock saveClock(Clock clock) {
        Update update = Update.update("schedule", clock.getSchedule());
        return mongo.findAndModify(
                Query.query(Criteria.where("name").is(clock.getName())),
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                Clock.class);
    }
}
