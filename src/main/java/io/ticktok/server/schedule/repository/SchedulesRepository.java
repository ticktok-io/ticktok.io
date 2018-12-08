package io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SchedulesRepository extends MongoRepository<Schedule, String>, CustomSchedulesRepository {

    Optional<Schedule> findBySchedule(String schedule);

}
