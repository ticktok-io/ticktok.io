package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoHttpQueuesRepository implements HttpQueuesRepository {

    public static final String LAST_ACCESSED_TIME = "lastAccessedTime";
    public static final String SCHEDULE = "schedule";
    public static final String NAME = "name";
    public static final String QUEUE_TTL = "queueTTL";
    public static final String EXTERNAL_ID = "externalId";

    private final MongoOperations mongo;
    private final long queueTTL;

    public MongoHttpQueuesRepository(
            String queueTTL,
            MongoOperations mongo) {
        this.queueTTL = Long.parseLong(queueTTL);
        this.mongo = mongo;
    }

    @PostConstruct
    public void createTTLIndex() {
        dropTTLIndexIfNeeded();
        mongo.indexOps(HttpQueue.class).ensureIndex(
                new Index().named(QUEUE_TTL).on(LAST_ACCESSED_TIME, Sort.Direction.ASC).expire(queueTTL, TimeUnit.MILLISECONDS));
    }

    private void dropTTLIndexIfNeeded() {
        try {
            mongo.indexOps(HttpQueue.class).dropIndex(QUEUE_TTL);
        } catch (UncategorizedMongoDbException e) {
            // Index not found, ignore
        }
    }

    @Override
    public List<TickMessage> pop(String externalId) {
        HttpQueue httpQueue = mongo.findAndModify(
                query(where(EXTERNAL_ID).is(externalId)),
                new OnPopUpdate().create(),
                HttpQueue.class);
        validateQueueExists(externalId, httpQueue);
        return httpQueue.getTicks();
    }

    private void validateQueueExists(String id, HttpQueue httpQueue) {
        if (httpQueue == null) {
            throw new QueueNotExistsException(String.format("Queue with id: %s not exists", id));
        }
    }

    @Override
    public HttpQueue createQueue(String name) {
        return mongo.findAndModify(
                query(where(NAME).is(name)),
                Update.update(LAST_ACCESSED_TIME, new Date()).setOnInsert(EXTERNAL_ID, UUID.randomUUID().toString()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                HttpQueue.class);
    }

    @Override
    public void push(TickMessage tickMessage) {
        mongo.updateMulti(query(where(SCHEDULE).is(tickMessage.getSchedule())),
                updateForTick(tickMessage), HttpQueue.class);
    }

    private Update updateForTick(TickMessage tickMessage) {
        return new Update().push("ticks", tickMessage);
    }

    @Override
    public void push(String queueName, TickMessage tickMessage) {
        mongo.updateFirst(query(where(NAME).is(queueName)),
                updateForTick(tickMessage), HttpQueue.class);
    }

    @Override
    public void deleteQueue(String name) {
        mongo.remove(query(where(NAME).is(name)), HttpQueue.class);
    }

    @Override
    public boolean isQueueExists(String queueName) {
        return mongo.exists(query(where(NAME).is(queueName)), HttpQueue.class);
    }

    @Override
    public void updateQueueSchedule(String queueName, String schedule) {
        mongo.upsert(
                query(where(NAME).is(queueName)),
                Update.update(SCHEDULE, schedule),
                HttpQueue.class);
    }

    public static class OnPopUpdate {
        public Update create() {
            return new Update()
                    .unset("ticks")
                    .set(LAST_ACCESSED_TIME, now());
        }

        protected Date now() {
            return new Date();
        }
    }
}
