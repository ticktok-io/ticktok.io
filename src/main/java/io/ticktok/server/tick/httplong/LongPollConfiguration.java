package io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.http.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("http-long")
public class LongPollConfiguration {

    public static final String POLL_PATH = "/api/v1/channels/poll";

    @Bean
    public TickChannelOperations tickChannelExplorer() {
        return new LongPollTickChannelOperations();
    }

    @Bean
    public TickPublisher tickPublisher() {
        return new LongPollTickPublisher();
    }

    @Bean
    public ChannelsRepository channelsRepository() {
        return new MongoChannelsRepository();
    }
}
