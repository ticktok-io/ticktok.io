package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


class QueueNameCreatorTest {

    @Test
    void createDifferentNamesForDifferentConsumers() {
        assertNotEquals(
                QueueName.createNameFor(new Clock("123", "schedule", "kuku")),
                QueueName.createNameFor(new Clock("456", "schedule", "popo")));
    }


}