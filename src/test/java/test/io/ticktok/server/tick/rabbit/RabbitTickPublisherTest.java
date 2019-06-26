package test.io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.TickPublisher;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import io.ticktok.server.tick.rabbit.RabbitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.IntegrationTest;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RabbitConfiguration.class})
@EnableConfigurationProperties(RabbitProperties.class)
@IntegrationTest
class RabbitTickPublisherTest {

    public static final Clock CLOCK = new Clock("koko", "every.999.seconds");
    public static final String QUEUE_NAME = new QueueNameCreator(CLOCK).create();
    public static final Queue QUEUE = new Queue(QUEUE_NAME, false, false, true);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private TopicExchange exchange;
    @Autowired
    private TickPublisher tickPublisher;


    @BeforeEach
    void emptyQueue() {
        amqpAdmin.declareQueue(QUEUE);
    }

    private Object receivedTick() {
        try {
            return rabbitTemplate.receiveAndConvert(QUEUE_NAME, 500);
        } catch (AmqpIOException e) {
            return null;
        }
    }

    @Test
    void shouldDelegateSchedule() {
        bindWith("every.10.seconds");
        tickPublisher.publish(Tick.create("every.10.seconds", 0));
        final TickMessage tickMessage = new Gson().fromJson((String) rabbitTemplate.receiveAndConvert(QUEUE_NAME), TickMessage.class);
        assertThat(tickMessage.getSchedule()).isEqualTo("every.10.seconds");
    }

    private void bindWith(String routing) {
        amqpAdmin.declareBinding(BindingBuilder.bind(QUEUE).to(exchange).with(routing));
    }

    @Test
    void publishTickForSpecificClock() {
        tickPublisher.publishForClock(CLOCK);
        assertThat(receivedTick())
                .isNotNull()
                .extracting((e) -> new Gson().fromJson((String) e, TickMessage.class).getSchedule())
                .isEqualTo(CLOCK.getSchedule());
    }

    @Test
    void shouldDeleteMessageOnTTL() throws InterruptedException {
        bindWith("every.1.seconds");
        tickPublisher.publish(Tick.create("every.1.seconds", 0));
        sleep(1000);
        assertThat(rabbitTemplate.receiveAndConvert(QUEUE_NAME)).isNull();
    }
}