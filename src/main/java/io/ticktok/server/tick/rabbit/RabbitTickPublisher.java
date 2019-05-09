package io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
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

    @Override
    public void publishForClock(Clock clock) {
        rabbitOperations.convertAndSend(new QueueNameCreator(clock).create(), tickMessageFor(clock.getSchedule()));
    }

    private String tickMessageFor(String schedule) {
        return new Gson().toJson(new TickMessage(schedule));
    }
}
