package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MongoHttpQueuesRepository implements HttpQueuesRepository {

    public static final String LAST_ACCESSED_TIME = "lastAccessedTime";

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
        try {
            mongo.indexOps(HttpQueue.class).dropIndex("queueTTL");
        } catch (UncategorizedMongoDbException e) {
            // Index not found, ignore
        }
        mongo.indexOps(HttpQueue.class).ensureIndex(
                new Index().named("queueTTL").on(LAST_ACCESSED_TIME, Sort.Direction.ASC).expire(queueTTL, TimeUnit.MILLISECONDS));
    }

    @Override
    public List<TickMessage> pop(String id) {
        HttpQueue httpQueue = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(id)),
                new OnPopUpdate().create(),
                HttpQueue.class);
        validateQueueExists(id, httpQueue);
        return httpQueue.getTicks();
    }

    private void validateQueueExists(String id, HttpQueue httpQueue) {
        if (httpQueue == null) {
            throw new QueueNotExistsException(String.format("Queue with id: %s not exists", id));
        }
    }

    @Override
    public HttpQueue createQueue(String name, String schedule) {
        return mongo.findAndModify(
                Query.query(Criteria.where("name").is(name)),
                Update.update("schedule", schedule).set(LAST_ACCESSED_TIME, new Date()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                HttpQueue.class);
    }

    @Override
    public void push(TickMessage tickMessage) {
        mongo.updateMulti(Query.query(Criteria.where("schedule").is(tickMessage.getSchedule())),
                new Update().push("ticks", tickMessage), HttpQueue.class);
    }

    @Override
    public void deleteQueue(String name) {
        mongo.remove(Query.query(Criteria.where("name").is(name)), HttpQueue.class);
    }

    @Override
    public boolean isQueueExists(String queueName) {
        return mongo.exists(Query.query(Criteria.where("name").is(queueName)), HttpQueue.class);
    }

    @Override
    public void updateQueueSchedule(String queueName, String schedule) {
        mongo.upsert(
                Query.query(Criteria.where("name").is(queueName)),
                Update.update("schedule", schedule),
                HttpQueue.class);
    }

    public static class OnPopUpdate {
        public Update create() {
            return Update.update("ticks", new ArrayList<>())
                    .set(LAST_ACCESSED_TIME, now());
        }

        protected Date now() {
            return new Date();
        }
    }
}
