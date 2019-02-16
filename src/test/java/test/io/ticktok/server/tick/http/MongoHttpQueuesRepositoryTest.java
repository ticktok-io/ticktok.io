package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueue;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpQueuesRepository.QueueNotExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@DataMongoTest
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoHttpQueuesRepositoryTest.TestConfiguration.class})
@ActiveProfiles({"http"})
class MongoHttpQueuesRepositoryTest {

    @Configuration
    @ComponentScan(basePackages = {"io.ticktok.server.tick.http"})
    static class TestConfiguration {

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    public static final String SCHEDULE = "every.9.seconds";
    public static final String CLOCK_1_ID = "1415";
    public static final String CLOCK_2_ID = "73529";

    private List<String> createdQueues = new ArrayList<>();

    @Autowired
    private HttpQueuesRepository repository;

    @AfterEach
    void tearDown() {
        createdQueues.forEach(q -> repository.deleteQueue(q));
        createdQueues.clear();
    }

    @Test
    void failOnNonExistingQueue() {
        assertThrows(QueueNotExistsException.class, () -> repository.pop("unknown-queue-id"));
    }

    @Test
    void createNewQueue() {
        HttpQueue queue = createQueue("kuku", "every.666.seconds");
        assertThat(repository.isQueueExists(queue.getName())).isTrue();
        assertThat(repository.pop(queue.getId())).isEmpty();
    }

    private HttpQueue createQueue(String name, String schedule) {
        createdQueues.add(name);
        return repository.createQueue(name, schedule);
    }

    @Test
    void retrievePushedTicks() {
        HttpQueue queue = createQueue("q-name", "every.666.seconds");
        repository.push(new TickMessage("every.666.seconds"));
        repository.push(new TickMessage("every.666.seconds"));
        assertThat(repository.pop(queue.getId())).containsExactly(
                new TickMessage("every.666.seconds"), new TickMessage("every.666.seconds"));
    }

    @Test
    void deleteQueue() {
        HttpQueue queue = createQueue("popov", "every.555.seconds");
        repository.deleteQueue("popov");
        assertThrows(QueueNotExistsException.class, () -> repository.pop(queue.getId()));
    }

    @Test
    void ignoreRecreatingAQueue() {
        HttpQueue queue = createQueue("popov", "every.555.seconds");
        assertThat(createQueue("popov", "every.555.seconds").getId()).isEqualTo(queue.getId());
    }

    @Test
    void popShouldClearQueue() {
        HttpQueue queue = createQueue("q-name", "every.666.seconds");
        repository.push(new TickMessage("every.666.seconds"));
        assertThat(repository.pop(queue.getId())).hasSize(1);
        assertThat(repository.pop(queue.getId())).hasSize(0);
    }

    @Test
    void shouldBeFalseOnNonExistingQueue() {
        assertThat(repository.isQueueExists("no-name")).isFalse();
    }

    @Test
    void shouldAlterAssignedSchedule() {
        HttpQueue queue = createQueue("q-name", "every.666.seconds");
        repository.updateQueueSchedule("q-name", "");
        repository.push(new TickMessage("every.666.seconds"));
        assertThat(repository.pop(queue.getId())).isEmpty();
    }
}