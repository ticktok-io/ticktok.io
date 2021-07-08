package io.ticktok.server.tick.httplong;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoChannelsRepository implements ChannelsRepository {


    private final MongoOperations mongo;

    public MongoChannelsRepository(MongoOperations mongo) {
        this.mongo = mongo;
    }

    @Override
    public void updateLastPollTime(List<String> keys) {
        List<TicksChannel> channels = mongo.find(query(where("key").in(keys)), TicksChannel.class);
        if (channels.size() < keys.size())
            throw new ChannelNotExistsException();
    }

    @Override
    public TicksChannel createFor(String clockId, String schedule) {
        mongo.insert()
        return mongo.findAndModify(
                query(where("clockId").is(clockId)),
                new Update().setOnInsert("key", generateChannelKey()),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                TicksChannel.class);
    }

    @NotNull
    private String generateChannelKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
