package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
@Profile("http")
public class HttpConfiguration {

    public static final String POP_PATH = "/api/v1/queues/{id}/pop";

    @Bean
    public TickChannelExplorer tickChannelExplorer(HttpQueuesRepository queuesRepository) {
        return new HttpTickChannelExplorer(queuesRepository);
    }

    @Bean
    public TickPublisher tickPublisher(HttpQueuesRepository repository) {
        return new HttpTickPublisher(repository);
    }

    @Bean
    public HttpQueuesRepository queuesRepository(@Value("${queues.ttl}") String queueTTL, MongoOperations mongoOperations) {
        return new MongoHttpQueuesRepository(queueTTL, mongoOperations);
    }
}
