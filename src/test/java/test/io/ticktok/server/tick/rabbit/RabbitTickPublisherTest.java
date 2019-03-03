package test.io.ticktok.server.tick.rabbit;

import com.google.gson.Gson;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.rabbit.RabbitTickPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitTickPublisherTest {

    private final RabbitOperations rabbitOperations = mock(RabbitOperations.class);
    private final Exchange exchange = mock(Exchange.class);

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
}