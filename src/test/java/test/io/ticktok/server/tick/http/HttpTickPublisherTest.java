package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueName;
import io.ticktok.server.tick.Tick;
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
        httpTickPublisher.publish(Tick.create("schedule", 0));
        verify(queuesRepository).push("schedule");
    }

    @Test
    void addTickForSpecificClock() {
        final Clock clock = new Clock("lala", "every.111.seconds");
        httpTickPublisher.publishForClock(clock);
        verify(queuesRepository).push(QueueName.createNameFor(clock), new TickMessage(clock.getSchedule()));
    }
}