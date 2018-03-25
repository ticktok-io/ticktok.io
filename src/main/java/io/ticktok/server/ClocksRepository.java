package io.ticktok.server;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClocksRepository extends MongoRepository<Clock, String> {

}
