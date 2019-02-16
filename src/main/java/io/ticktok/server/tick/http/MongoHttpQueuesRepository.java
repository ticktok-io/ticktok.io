package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

public class MongoHttpQueuesRepository implements HttpQueuesRepository {

    private final MongoOperations mongo;

    public MongoHttpQueuesRepository(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public List<TickMessage> pop(String id) {
        HttpQueue httpQueue = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(id)),
                Update.update("ticks", new ArrayList<>()),
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
                Update.update("schedule", schedule),
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
}
