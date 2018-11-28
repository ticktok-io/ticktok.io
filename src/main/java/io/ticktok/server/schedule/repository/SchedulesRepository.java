package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SchedulesRepository extends MongoRepository<Schedule, String>, UpdateSchedulesRepository {

    List<Schedule> findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(int clocks, long time);

    Optional<Schedule> findBySchedule(String schedule);

    void deleteByClockCount(int clockCount);
}
