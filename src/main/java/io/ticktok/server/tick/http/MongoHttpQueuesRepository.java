package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.ArrayList;
import java.util.List;

public class MongoHttpQueuesRepository implements HttpQueuesRepository {


    private final MongoOperations mongo;

    public MongoHttpQueuesRepository(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public List<TickMessage> pop(String clockId) {
        return new ArrayList<>();
    }

    @Override
    public void add(String schedule) {

    }

    @Override
    public void assignClock(String clockId, String schedule) {
    }
}
