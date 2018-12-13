package io.ticktok.server.tick.repository;

import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;
import io.ticktok.server.tick.Tick;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static jdk.nashorn.internal.objects.NativeString.slice;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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

    @Override
    public void deletePublishedExceptLastPerSchedule(int count) {
        SortOperation sortByTime = sort(new Sort(Sort.Direction.DESC, "time"));
        //MatchOperation f = match(new Criteria().)
        //GroupOperation groupBySchedule = group("schedule").push("ROOT").as("docs");
        ProjectionOperation top10 = project().and(slice("count", 1)).as("docs");
        Aggregation aggregation = newAggregation(
                sortByTime, top10);
        AggregationResults<Tick> result = mongo.aggregate(aggregation, "tick", Tick.class);
        System.out.println(result);



    }

    private void verifyUpdated(UpdateResult result, String id) {
        if(result.getModifiedCount() == 0) {
            throw new TicksRepository.UnableToUpdateStatusException("Unable to update tick: " + id);
        }
    }
}
