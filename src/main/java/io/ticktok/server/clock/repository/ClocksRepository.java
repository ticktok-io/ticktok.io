package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClocksRepository extends MongoRepository<Clock, String>, UpdateClockRepository {

}

