package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpTickPublisher;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

class HttpTickPublisherTest {

    private final HttpQueuesRepository queuesRepository = mock(HttpQueuesRepository.class);
    private final ClocksRepository clocksRepository = mock(ClocksRepository.class);
    private final HttpTickPublisher httpTickPublisher = new HttpTickPublisher(queuesRepository, clocksRepository);

    @Test
    void addNewTicksToQueue() {
        Clock clock1 = new Clock("11447711", "kuku", "every.666.seconds");
        Clock clock2 = new Clock("11445433", "popo", clock1.getId());
        when(clocksRepository.findBySchedule(clock1.getSchedule())).thenReturn(asList(clock1, clock2));
        httpTickPublisher.publish(clock1.getSchedule());
        //verify(queuesRepository).add(asList(clock1.getId(), clock2.getId()), new TickMessage(clock1.getSchedule()));
    }

}