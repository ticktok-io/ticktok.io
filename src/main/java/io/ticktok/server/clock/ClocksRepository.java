package io.ticktok.server.clock;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClocksRepository extends MongoRepository<Clock, String>, UpdateClocksRepository {

}
