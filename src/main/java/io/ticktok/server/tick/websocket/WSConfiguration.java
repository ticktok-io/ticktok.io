package io.ticktok.server.tick.websocket;

import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("websocket")
public class WSConfiguration {

    @Bean
    public TickChannelOperations tickChannelExplorer() {
        return new WSTickChannelOperations();
    }

    @Bean
    public TickPublisher tickPublisher() {
        return new WSTickPublisher();
    }
}
