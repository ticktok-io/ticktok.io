package io.ticktok.server.tick.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
@Data
public class RabbitProperties {

    @Data
    public static class Queue {
        private String ttl;
    }

    private String uri;
    private String consumerUri;
    private Queue queue;

    public String getConsumerUri() {
        return consumerUri == null ? uri : consumerUri;
    }

    public String queueTTL() {
        return queue.getTtl();
    }
}
