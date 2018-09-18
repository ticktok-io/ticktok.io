package io.ticktok.server.tick;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class TicksRepositoryImpl implements UpdateTicksRepository {


    private final MongoOperations mongo;

    public TicksRepositoryImpl(MongoOperations mongoOperations) {
        this.mongo = mongoOperations;
    }

    @Override
    public void updateTickStatus(String id, String status) {
         UpdateResult result = mongo.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                Update.update("status", status),
                Tick.class);
         verifyUpdated(result, id);
    }

    private void verifyUpdated(UpdateResult result, String id) {
        if(result.getModifiedCount() == 0) {
            throw new TicksRepository.UnableToUpdateStatusException("Unable to update tick: " + id);
        }
    }
}
