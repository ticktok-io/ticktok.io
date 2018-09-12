package io.ticktok.server.tick;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicksRepository extends MongoRepository<Tick, String> {
}
