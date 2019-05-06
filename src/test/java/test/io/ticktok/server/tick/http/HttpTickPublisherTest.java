package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpTickPublisher;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HttpTickPublisherTest {

    private final HttpQueuesRepository queuesRepository = mock(HttpQueuesRepository.class);
    private final HttpTickPublisher httpTickPublisher = new HttpTickPublisher(queuesRepository);

    @Test
    void addNewTicksToQueue() {
        httpTickPublisher.publish("schedule");
        verify(queuesRepository).push(new TickMessage("schedule"));
    }

    @Test
    void addTickForSpecificClock() {
        final Clock clock = new Clock("lala", "every.111.seconds");
        httpTickPublisher.publishForClock(clock);
        verify(queuesRepository).push(new QueueNameCreator(clock).create(), new TickMessage(clock.getSchedule()));
    }
}