package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitTickChannelExplorer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@SpringBootTest
class RabbitTickChannelExplorerTest {

    @Autowired
    private RabbitTickChannelExplorer tickChannelExplorer;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Exchange exchange;

    private TickChannel channel;

    @Test
    void retrieveTrueWhenQueueExists() {
        Clock clock = new Clock("kuku", "every.111.seconds");
        channel = tickChannelExplorer.create(clock);
        assertTrue(tickChannelExplorer.isExists(clock));
    }

    @Test
    void createQueueForConsumer() {
        Clock clock = new Clock("kuku", "popov-schedule");
        TickChannel tickChannel = tickChannelExplorer.create(clock);
        rabbitTemplate.convertAndSend(exchange.getName(), clock.getSchedule(), "hello");
        assertThat(rabbitTemplate.receiveAndConvert(tickChannel.getQueue(), 1000), is("hello"));
    }

    @AfterEach
    void deleteQueue() {
        if(channel != null) {
            amqpAdmin.deleteQueue(channel.getQueue());
        }

    }
}