package io.ticktok.server.schedule.repository;

import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import io.ticktok.server.clock.Clock;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ScheduleUpdater {

    private final MongoTemplate mongo;

    public ScheduleUpdater(MongoTemplate mongoOperations, SchedulesRepository schedulesRepository) {
        this.mongo = mongoOperations;
    }

    @PostConstruct
    public void listen() {
        MongoCollection<Document> collection = mongo.getDb().getCollection("clock");
        collection.watch().forEach((Block<? super ChangeStreamDocument<Document>>) c -> {
            System.out.println(c);
        });
    }
}
