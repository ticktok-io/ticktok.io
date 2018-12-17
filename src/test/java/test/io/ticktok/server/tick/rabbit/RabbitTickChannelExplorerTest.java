package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitProperties;
import io.ticktok.server.tick.rabbit.RabbitTickChannelExplorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.lang.Thread.sleep;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@EnableConfigurationProperties(RabbitProperties.class)
@SpringBootTest
class RabbitTickChannelExplorerTest {

    public static final Clock CLOCK = new Clock("kuku", "every.111.seconds");

    @Autowired
    private RabbitTickChannelExplorer tickChannelExplorer;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Exchange exchange;

    private TickChannel channel;

    @BeforeEach
    void createChannel() {
        channel = tickChannelExplorer.create(CLOCK);
    }

    @Test
    void retrieveTrueWhenQueueExists() {
        assertTrue(tickChannelExplorer.isExists(CLOCK));
    }

    @Test
    void createQueueForConsumer() {
        rabbitTemplate.convertAndSend(exchange.getName(), CLOCK.getSchedule(), "hello");
        assertThat(rabbitTemplate.receiveAndConvert(channel.getQueue(), 500), is("hello"));
    }

    @Test
    void channelShouldDeleteIfUnused() throws InterruptedException {
        sleep(1000);
        assertNull(amqpAdmin.getQueueProperties(channel.getQueue()));
    }

}