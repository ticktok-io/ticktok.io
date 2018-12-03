package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClocksRepository extends MongoRepository<Clock, String>, UpdateClockRepository {

    List<Clock> findByStatus(String status);

}

