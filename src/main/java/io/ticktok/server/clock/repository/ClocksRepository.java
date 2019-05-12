package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClocksRepository extends MongoRepository<Clock, String>, CustomClockRepository {

    List<Clock> findByStatus(String status);

    static String not(String status) {
        return "!" + status;
    }

}
