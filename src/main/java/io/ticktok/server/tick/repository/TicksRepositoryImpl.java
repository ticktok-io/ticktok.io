package io.ticktok.server.tick.repository;

import com.mongodb.client.result.UpdateResult;
import io.ticktok.server.tick.Tick;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

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

    private void verifyUpdated(UpdateResult result, String id) {
        if(result.getModifiedCount() == 0) {
            throw new TicksRepository.UnableToUpdateStatusException("Unable to update tick: " + id);
        }
    }

    @Override
    public void deletePublishedExceptLastPerSchedule(int count) {
        mongo.remove(
                Query.query(Criteria
                        .where("status").is(Tick.PUBLISHED)
                        .and("id").nin(getLastXPublishedTicksPerSchedule(count))),
                Tick.class);
    }

    private List<String> getLastXPublishedTicksPerSchedule(int count) {
        MatchOperation matchPublished = match(Criteria.where("status").is(Tick.PUBLISHED));
        SortOperation sortByTime = sort(new Sort(Sort.Direction.DESC, "time"));
        GroupOperation groupBySchedule = group("schedule").push("_id").as("tickId");
        ProjectionOperation topX = project().and("tickId").slice(count).as("tickId");
        UnwindOperation unwind = unwind("tickId");
        Aggregation aggregation = newAggregation(matchPublished, sortByTime, groupBySchedule, topX, unwind);
        AggregationResults<RedundantTick> result = mongo.aggregate(aggregation, "tick", RedundantTick.class);
        return result.getMappedResults().stream().map(RedundantTick::getTickId).collect(Collectors.toList());
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    class RedundantTick {

        private String id;
        @Id
        private String tickId;
    }
}
