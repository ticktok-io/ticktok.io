package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

public class RabbitTickChannelFactory implements TickChannelFactory {

    private final AmqpAdmin rabbitAdmin;
    private final String rabbitUri;
    private final TopicExchange exchange;

    public RabbitTickChannelFactory(AmqpAdmin rabbitAdmin, String rabbitUri, TopicExchange topicExchange) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitUri = rabbitUri;
        this.exchange = topicExchange;
    }

    @Override
    public TickChannel create(Clock clock) {
        Queue queue = new Queue(new QueueNameCreator(clock).create());
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(clock.getSchedule()));
        return new TickChannel(rabbitUri, queue.getName());
    }

}
