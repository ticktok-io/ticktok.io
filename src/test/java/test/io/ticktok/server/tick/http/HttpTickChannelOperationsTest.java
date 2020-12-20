package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueName;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.http.HttpQueue;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpTickChannelOperations;
import org.junit.jupiter.api.Test;

import static io.ticktok.server.tick.http.HttpConfiguration.POP_PATH;
import static io.ticktok.server.tick.http.HttpTickChannelOperations.URL_PARAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

class HttpTickChannelOperationsTest {

    public static final Clock CLOCK = new Clock("1122", "popo", "every.911.seconds");

    private HttpQueuesRepository queuesRepository = mock(HttpQueuesRepository.class);
    private HttpTickChannelOperations tickChannelExplorer = new HttpTickChannelOperations(queuesRepository);

    @Test
    void createNewQueue() {
        HttpQueue httpQueue = HttpQueue.builder().id("1212").externalId("32415").build();
        when(queuesRepository.createQueue(queueName())).thenReturn(httpQueue);
        TickChannel tickChannel = tickChannelExplorer.create(CLOCK);
        assertThat(tickChannel.getDetails()).contains(entry(URL_PARAM,
                "{domain}" + POP_PATH.replaceAll("\\{id}", httpQueue.getExternalId())));
    }

    private String queueName() {
        return QueueName.createNameFor(CLOCK);
    }

    @Test
    void isQueueExists() {
        when(queuesRepository.isQueueExists(queueName())).thenReturn(true);
        assertThat(tickChannelExplorer.isExists(CLOCK)).isTrue();
        when(queuesRepository.isQueueExists(queueName())).thenReturn(false);
        assertThat(tickChannelExplorer.isExists(CLOCK)).isFalse();
    }

    @Test
    void disableQueue() {
        tickChannelExplorer.disable(CLOCK);
        verify(queuesRepository).updateQueueSchedule(queueName(), "");
    }

    @Test
    void enableQueue() {
        tickChannelExplorer.enable(CLOCK);
        verify(queuesRepository).updateQueueSchedule(queueName(), CLOCK.getSchedule());
    }
}