package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelCreator;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitManagementTemplate;

public class RabbitTickChannelCreator implements TickChannelCreator {

    private final AmqpAdmin rabbitAdmin;
    private final String rabbitUri;
    private final TopicExchange exchange;
    private RabbitManagementTemplate rabbitManagementTemplate;

    public RabbitTickChannelCreator(AmqpAdmin rabbitAdmin, String rabbitUri, TopicExchange topicExchange) {
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitUri = rabbitUri;
        this.exchange = topicExchange;
    }

    @Override
    public TickChannel create(String name, String schedule) {
        Queue queue = new Queue(new QueueNameCreator(name, schedule).create(), true, false, true);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(schedule));
        return new TickChannel(rabbitUri, queue.getName());
    }

}
