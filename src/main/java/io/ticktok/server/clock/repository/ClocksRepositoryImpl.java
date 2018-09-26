package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ClocksRepositoryImpl implements UpdateClocksRepository {

    private final MongoOperations mongo;

    public ClocksRepositoryImpl(MongoOperations mongoOperations) {
        this.mongo = mongoOperations;
    }

    @Override
    public void updateLatestScheduledTick(String id, long time) {
        mongo.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                Update.update("latestScheduledTick", time),
                Clock.class);
    }
}
