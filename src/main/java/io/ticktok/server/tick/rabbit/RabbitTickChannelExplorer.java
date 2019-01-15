package io.ticktok.server.tick.rabbit;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelExplorer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;

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
        declareBindingFor(clock);
        return new TickChannel(consumerRabbitUri, queue.getName());
    }

    private Queue queueFor(Clock clock) {
        return new Queue(nameFor(clock), true, false, true, queueOptions());
    }

    private ImmutableMap<String, Object> queueOptions() {
        return ImmutableMap.of("x-expires", queueTTL);
    }

    private void declareBindingFor(Clock clock) {
        if(clockQueueExists(clock)) {
            rabbitAdmin.declareBinding(clockBinding(clock));
        }
    }

    private Binding clockBinding(Clock clock) {
        return BindingBuilder.bind(queueFor(clock)).to(exchange).with(clock.getSchedule());
    }

    @Override
    public void disable(Clock clock) {
        rabbitAdmin.removeBinding(clockBinding(clock));
    }

    @Override
    public void enable(Clock clock) {
        declareBindingFor(clock);
    }
}
