package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.httplong.ChannelsRepository;
import io.ticktok.server.tick.httplong.MongoChannelsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
@Profile("http")
public class HttpConfiguration {

    public static final String POP_PATH = "/api/v1/queues/{id}/pop";
    public static final String POLL_PATH = "/api/v1/channels/{id}/poll";

    public static String popPathForId(String id) {
       return POP_PATH.replaceAll("\\{id}", id);
    }

    public static String pollPathForId(String id) {
        return POP_PATH.replaceAll("\\{id}", id);
    }

    @Bean
    public TickChannelOperations tickChannelExplorer(HttpQueuesRepository queuesRepository) {
        return new HttpTickChannelOperations(queuesRepository);
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
