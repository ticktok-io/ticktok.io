package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelFactory;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitTickPublisher implements TickPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TickChannelFactory tickChannelFactory;

    @Autowired
    public RabbitTickPublisher(RabbitTemplate rabbitTemplate, TickChannelFactory tickChannelFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.tickChannelFactory = tickChannelFactory;
    }

    @Override
    public void publish(String schedule) {
        TickChannel channel = tickChannelFactory.createForSchedule(schedule);
        rabbitTemplate.convertAndSend(channel.getExchange(), channel.getTopic(), "tick");
        System.out.println("Tick sent");
    }
}
