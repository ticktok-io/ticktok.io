package io.ticktok.server.tick.rabbit;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelExplorer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@Slf4j
public class RabbitTickChannelExplorer implements TickChannelExplorer {

    private final long queueTTL;
    private final AmqpAdmin rabbitAdmin;
    private final String consumerRabbitUri;
    private final TopicExchange exchange;

    public RabbitTickChannelExplorer(
            RabbitProperties rabbitProperties,
            AmqpAdmin rabbitAdmin,
            TopicExchange topicExchange) {
        this.queueTTL = Long.valueOf(rabbitProperties.queueTTL());
        this.rabbitAdmin = rabbitAdmin;
        this.consumerRabbitUri = rabbitProperties.getConsumerUri();
        this.exchange = topicExchange;
    }

    @Override
    public boolean isExists(Clock clock) {
        return rabbitAdmin.getQueueProperties(nameFor(clock)) != null;
    }

    private String nameFor(Clock clock) {
        return new QueueNameCreator(clock).create();
    }

    @Override
    public TickChannel create(Clock clock) {
        log.info("Rabbit URI: {}", consumerRabbitUri);
        Queue queue = new Queue(nameFor(clock), true, false, true, queueOptions());
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(clock.getSchedule()));
        return new TickChannel(consumerRabbitUri, queue.getName());
    }

    private ImmutableMap<String, Object> queueOptions() {
        return ImmutableMap.of("x-expires", queueTTL);
    }
}
