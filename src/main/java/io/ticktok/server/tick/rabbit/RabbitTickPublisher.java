package io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class RabbitTickPublisher implements TickPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public RabbitTickPublisher(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publish(String schedule) {
        rabbitTemplate.convertAndSend(exchange.getName(), schedule, new Gson().toJson(new TickMessage("")));
    }
}
