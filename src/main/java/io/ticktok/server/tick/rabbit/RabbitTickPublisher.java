package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.ClockChannel;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitTickPublisher implements TickPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    @Autowired
    public RabbitTickPublisher(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    @Override
    public void publish(String name) {
        rabbitTemplate.convertAndSend(topicExchange.getName(), name, "tick");
        System.out.println("Tick sent");
    }
}
