package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpTickPublisher;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

class HttpTickPublisherTest {

    private HttpQueuesRepository queuesRepository = mock(HttpQueuesRepository.class);
    private ClocksRepository clocksRepository = mock(ClocksRepository.class);

    @Test
    void addNewTickToQueue() {
        Clock clock = new Clock("11447711", "kuku", "every.666.seconds");
        when(clocksRepository.findBySchedule(clock.getSchedule())).thenReturn(singletonList(clock));
        new HttpTickPublisher(queuesRepository, clocksRepository).publish(clock.getSchedule());
        verify(queuesRepository).add(asList("11447711"), new TickMessage(clock.getSchedule()));
    }
}