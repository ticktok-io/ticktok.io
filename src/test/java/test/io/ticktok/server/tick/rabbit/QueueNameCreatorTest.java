package test.io.ticktok.server.tick.rabbit;

import io.ticktok.server.tick.rabbit.QueueNameCreator;
import io.ticktok.server.tick.rabbit.RabbitConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;


class QueueNameCreatorTest {

    @Test
    void createDifferentNamesForDifferentConsumers() {
        assertNotEquals(
                new QueueNameCreator("schedule", "kuku").create(),
                new QueueNameCreator("schedule", "popo").create());
    }
}