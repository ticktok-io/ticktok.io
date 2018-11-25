package io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannelExplorer;
import org.springframework.amqp.core.AmqpAdmin;

public class RabbitTickChannelExplorer implements TickChannelExplorer {

    private final AmqpAdmin rabbitAdmin;

    public RabbitTickChannelExplorer(AmqpAdmin amqpAdmin) {
        this.rabbitAdmin = amqpAdmin;
    }

    @Override
    public boolean isExists(String name) {
        return rabbitAdmin.getQueueProperties(name) != null;
    }
}
