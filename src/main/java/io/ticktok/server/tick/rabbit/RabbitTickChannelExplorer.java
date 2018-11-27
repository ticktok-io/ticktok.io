package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelExplorer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

public class RabbitTickChannelExplorer implements TickChannelExplorer {

    private final AmqpAdmin rabbitAdmin;
    private final String rabbitUri;
    private final TopicExchange exchange;

    public RabbitTickChannelExplorer(AmqpAdmin rabbitAdmin, String rabbitUri, TopicExchange topicExchange) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitUri = rabbitUri;
        this.exchange = topicExchange;
    }

    @Override
    public boolean isExists(Clock clock) {
        return rabbitAdmin.getQueueProperties(new QueueNameCreator(clock).create()) != null;
    }

    @Override
    public TickChannel create(Clock clock) {
        Queue queue = new Queue(new QueueNameCreator(clock).create(), true, false, true);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(clock.getSchedule()));
        return new TickChannel(rabbitUri, queue.getName());
    }
}
