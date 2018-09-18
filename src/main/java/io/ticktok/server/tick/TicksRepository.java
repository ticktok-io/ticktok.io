package io.ticktok.server.tick;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TicksRepository extends MongoRepository<Tick, String>, UpdateTicksRepository {

    List<Tick> findByStatusAndTimeLessThanEqual(String status, long time);


    class UnableToUpdateStatusException extends RuntimeException {
        public UnableToUpdateStatusException(String message) {
            super(message);
        }
    }

}
