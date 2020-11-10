package io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitOperations;

public class RabbitTickPublisher implements TickPublisher {

    private final RabbitOperations rabbitOperations;
    private final Exchange exchange;

    public RabbitTickPublisher(RabbitOperations rabbitOperations, Exchange exchange) {
        this.rabbitOperations = rabbitOperations;
        this.exchange = exchange;
    }

    @Override
    public void publish(Tick tick) {
        rabbitOperations.convertAndSend(
                exchange.getName(),
                tick.getSchedule(),
                tickMessageFor(tick.getSchedule()),
                new TicktokMessagePostProcessor(tick.ttl()));
    }

    private String tickMessageFor(String schedule) {
        return new Gson().toJson(new TickMessage(schedule));
    }

    @Override
    public void publishForClock(Clock clock) {
        rabbitOperations.convertAndSend(new QueueNameCreator(clock).create(), tickMessageFor(clock.getSchedule()));
    }

    private static class TicktokMessagePostProcessor implements MessagePostProcessor {

        private final int ttl;

        public TicktokMessagePostProcessor(int ttl) {
            this.ttl = ttl;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            message.getMessageProperties().setExpiration(String.valueOf(ttl));
            return message;
        }
    }
}
