package io.ticktok.server.tick.rabbit;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitTickChannelOperations implements TickChannelOperations {

    private final long queueTTL;
    private final AmqpAdmin rabbitAdmin;
    private final String consumerRabbitUri;
    private final TopicExchange exchange;

    public RabbitTickChannelOperations(
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
        return clockQueueExists(clock);
    }

    private boolean clockQueueExists(Clock clock) {
        return rabbitAdmin.getQueueProperties(nameFor(clock)) != null;
    }

    private String nameFor(Clock clock) {
        return new QueueNameCreator(clock).create();
    }

    @Override
    public TickChannel create(Clock clock) {
        log.info("Creating a queue for [name: {}, schedule: {}]", clock.getName(), clock.getSchedule());
        Queue queue = queueFor(clock);
        rabbitAdmin.declareQueue(queue);
        return createTickChannelFor(queue);
    }

    private Queue queueFor(Clock clock) {
        return new Queue(nameFor(clock), true, false, true, queueOptions());
    }

    private ImmutableMap<String, Object> queueOptions() {
        return ImmutableMap.of("x-expires", queueTTL);
    }

    private Binding clockBinding(Clock clock) {
        return BindingBuilder.bind(queueFor(clock)).to(exchange).with(clock.getSchedule());
    }

    private TickChannel createTickChannelFor(Queue queue) {
        return TickChannel.builder()
                .type(TickChannel.RABBIT)
                .uri(consumerRabbitUri)
                .queue(queue.getName())
                .details(ImmutableMap.of("uri", consumerRabbitUri, "queue", queue.getName()))
                .build();
    }

    @Override
    public void disable(Clock clock) {
        rabbitAdmin.removeBinding(clockBinding(clock));
    }

    @Override
    public void enable(Clock clock) {
        if (clockQueueExists(clock)) {
            rabbitAdmin.declareBinding(clockBinding(clock));
        }
    }

}
