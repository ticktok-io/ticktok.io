package io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.http.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
@Profile("http-long")
public class LongPollConfiguration {

    public static final String POLL_PATH = "/api/v1/channels/poll";

    @Bean
    public TickChannelOperations tickChannelExplorer(ChannelsRepository channelsRepository) {
        return new LongPollTickChannelOperations(channelsRepository);
    }

    @Bean
    public TickPublisher tickPublisher() {
        return new LongPollTickPublisher();
    }

    @Bean
    public ChannelsRepository channelsRepository(MongoOperations mongoOperations) {
        return new MongoChannelsRepository(mongoOperations);
    }
}
