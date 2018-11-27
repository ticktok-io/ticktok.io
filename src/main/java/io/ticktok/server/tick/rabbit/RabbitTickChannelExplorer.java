package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannelExplorer;
import org.springframework.amqp.core.AmqpAdmin;

public class RabbitTickChannelExplorer implements TickChannelExplorer {

    private final AmqpAdmin rabbitAdmin;

    public RabbitTickChannelExplorer(AmqpAdmin amqpAdmin) {
        this.rabbitAdmin = amqpAdmin;
    }

    @Override
    public boolean isExists(Clock clock) {
        return rabbitAdmin.getQueueProperties(new QueueNameCreator(clock).create()) != null;
    }
}
