package io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
@Profile("http")
public class HttpConfiguration {

    public static final java.lang.String POP_PATH = "/api/v1/queues/{id}/pop";

    @Bean
    public TickChannelExplorer tickChannelExplorer(HttpQueuesRepository httpQueuesRepository) {
        return new HttpTickChannelExplorer(httpQueuesRepository);
    }

    @Bean
    public TickPublisher tickPublisher(HttpQueuesRepository repository, ClocksRepository clocksRepository) {
        return new HttpTickPublisher(repository);
    }

    @Bean
    public HttpQueuesRepository queuesRepository(MongoOperations mongoOperations) {
        return new MongoHttpQueuesRepository(mongoOperations);
    }
}
