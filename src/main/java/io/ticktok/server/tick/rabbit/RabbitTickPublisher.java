package io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitOperations;

public class RabbitTickPublisher implements TickPublisher {

    private final RabbitOperations rabbitOperations;
    private final Exchange exchange;

    public RabbitTickPublisher(RabbitOperations rabbitOperations, Exchange exchange) {
        this.rabbitOperations = rabbitOperations;
        this.exchange = exchange;
    }

    @Override
    public void publish(String schedule) {
        rabbitOperations.convertAndSend(exchange.getName(), schedule, tickMessageFor(schedule));
    }

    private String tickMessageFor(String schedule) {
        return new Gson().toJson(new TickMessage(schedule));
    }
}
