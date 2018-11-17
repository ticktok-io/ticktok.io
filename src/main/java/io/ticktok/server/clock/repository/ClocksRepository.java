package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClocksRepository extends MongoRepository<Clock, String>, UpdateClockRepository {

    @Query("{ 'schedules.1' : { $exists: true } }")
    List<Clock> findByMoreThanOneSchedule();
}

