package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitTickChannelCreator;
import io.ticktok.server.tick.rabbit.RabbitTickChannelExplorer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@SpringBootTest
class RabbitTickChannelExplorerTest {

    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTickChannelCreator tickChannelFactory;
    @Autowired
    private RabbitTickChannelExplorer tickChannelExplorer;

    private TickChannel channel;

    @Test
    void retrieveTrueWhenQueueExists() {
        channel = tickChannelFactory.create("kuku", "every.111.seconds");
        assertTrue(tickChannelExplorer.isExists(channel.getQueue()));
    }

    @AfterEach
    void deleteQueue() {
        if(channel != null) {
            amqpAdmin.deleteQueue(channel.getQueue());
        }

    }
}