package io.ticktok.server.tick.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnBean(RabbitTemplate.class)
@ConditionalOnProperty(prefix = "management.health.rabbitmq", name = "enabled", matchIfMissing = true)
public class RabbitHealthIndicatorConfiguration {
}
