package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitProperties;
import io.ticktok.server.tick.rabbit.RabbitTickChannelOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.IntegrationTest;

import static java.lang.Thread.sleep;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@EnableConfigurationProperties(RabbitProperties.class)
@SpringBootTest(properties = {"rabbit.queue.ttl=500"})
@IntegrationTest
class RabbitTickChannelOperationsTest {

    public static final Clock CLOCK = new Clock("kuku", "every.111.seconds");
    public static final String TICK_MSG = "hello";

    @Autowired
    private RabbitTickChannelOperations tickChannelExplorer;
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
        tickChannelExplorer.enable(CLOCK);
        sendTick();
        assertThat(receivedTick(), is(TICK_MSG));
    }

    private void sendTick() {
        rabbitTemplate.convertAndSend(exchange.getName(), CLOCK.getSchedule(), TICK_MSG);
    }

    private Object receivedTick() {
        return rabbitTemplate.receiveAndConvert(channel.getQueue(), 500);
    }

    @Test
    void channelShouldBeDeletedIfUnused() throws InterruptedException {
        sleep(600);
        assertNull(amqpAdmin.getQueueProperties(channel.getQueue()));
    }

    @Test
    void shouldUnbindQueueOnDisable() {
        tickChannelExplorer.enable(CLOCK);
        tickChannelExplorer.disable(CLOCK);
        sendTick();
        assertNull(receivedTick());
    }

    @Test
    void shouldBindQueueOnEnable() {
        tickChannelExplorer.enable(CLOCK);
        sendTick();
        assertThat(receivedTick(), is(TICK_MSG));
    }

    @Test
    void ignoreEnableFailureIfQueueNotExists() {
        tickChannelExplorer.enable(new Clock("papa", "every.321.seconds"));
        // pass
    }
}