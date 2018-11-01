package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SchedulesRepository extends MongoRepository<Schedule, String>, UpdateSchedulesRepository {

    List<Schedule> findByLatestScheduledTickLessThanEqual(long time);

    void deleteBySchedule(String schedule);
}
