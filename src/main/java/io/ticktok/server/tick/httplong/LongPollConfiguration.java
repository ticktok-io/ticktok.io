package io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
@Profile("http-poll")
public class LongPollConfiguration {

    public static final String POLL_PATH = "/api/v1/channels/poll";

    @Bean
    public TickChannelOperations tickChannelExplorer(HttpQueuesRepository queuesRepository) {
        return new LongPollTickChannelOperations(queuesRepository);
    }

    @Bean
    public TickPublisher tickPublisher(HttpQueuesRepository repository) {
        return null;
    }

    @Bean
    public ChannelsRepository channelsRepository() {
        return new MongoChannelsRepository();
    }
}
