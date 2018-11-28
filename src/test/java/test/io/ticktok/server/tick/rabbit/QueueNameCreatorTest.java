package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.rabbit.QueueNameCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


class QueueNameCreatorTest {

    @Test
    void createDifferentNamesForDifferentConsumers() {
        assertNotEquals(
                new QueueNameCreator(new Clock("schedule", "kuku")).create(),
                new QueueNameCreator(new Clock("schedule", "popo")).create());
    }
}