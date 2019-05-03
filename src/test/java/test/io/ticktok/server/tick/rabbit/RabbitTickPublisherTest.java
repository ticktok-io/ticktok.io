package test.io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitProperties;
import io.ticktok.server.tick.rabbit.RabbitTickPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.IntegrationTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@EnableConfigurationProperties(RabbitProperties.class)
@IntegrationTest
class RabbitTickPublisherTest {

    public static final Clock CLOCK = new Clock("koko", "every.911..seconds");
    public static final String QUEUE_NAME = new QueueNameCreator(CLOCK).create();
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private TickPublisher tickPublisher;

    private final RabbitOperations rabbitOperations = mock(RabbitOperations.class);
    private final Exchange exchange = mock(Exchange.class);


    @BeforeEach
    void emptyQueue() {
        while(receivedTick() != null);
    }

    private Object receivedTick() {
        try {
            return rabbitTemplate.receiveAndConvert(QUEUE_NAME, 500);
        } catch(AmqpIOException e) {
            return null;
        }
    }

    @Test
    void shouldDelegateSchedule() {
        ArgumentCaptor<String> tickMessage = ArgumentCaptor.forClass(String.class);
        new RabbitTickPublisher(rabbitOperations, exchange).publish("kuku");
        verify(rabbitOperations).convertAndSend(any(), any(), tickMessage.capture());
        assertThat(scheduleFrom(tickMessage)).isEqualTo("kuku");
    }

    private String scheduleFrom(ArgumentCaptor<String> tickMessage) {
        return new Gson().fromJson(tickMessage.getValue(), TickMessage.class).getSchedule();
    }

    @Test
    void publishTickForSpecificClock() {
        amqpAdmin.declareQueue(new Queue(QUEUE_NAME));
        tickPublisher.publishForClock(CLOCK);
        assertThat(receivedTick())
                .isNotNull()
                .extracting((e) -> new Gson().fromJson((String)e, TickMessage.class).getSchedule())
                .isEqualTo(CLOCK.getSchedule());
    }
}