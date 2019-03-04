package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueue;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpQueuesRepository.QueueNotExistsException;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
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
import test.io.ticktok.server.support.IntegrationTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@DataMongoTest(properties = {"queues.ttl=500"})
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoHttpQueuesRepositoryTest.TestConfiguration.class})
@ActiveProfiles({"http"})
@IntegrationTest
class MongoHttpQueuesRepositoryTest {

    public static final String SCHEDULE = "every.666.seconds";

    @Configuration
    @ComponentScan(basePackages = {"io.ticktok.server.tick.http"})
    static class TestConfiguration {

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

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
        HttpQueue queue = createQueue("kuku");
        assertThat(repository.isQueueExists(queue.getName())).isTrue();
        assertThat(repository.pop(queue.getId())).isEmpty();
    }

    private HttpQueue createQueue(String name) {
        createdQueues.add(name);
        return repository.createQueue(name);
    }

    @Test
    void retrievePushedTicks() {
        HttpQueue queue = createQueue("q-name");
        repository.updateQueueSchedule("q-name", SCHEDULE);
        repository.push(new TickMessage(SCHEDULE));
        repository.push(new TickMessage(SCHEDULE));
        assertThat(repository.pop(queue.getId())).containsExactly(
                new TickMessage("every.666.seconds"), new TickMessage("every.666.seconds"));
    }

    @Test
    void deleteQueue() {
        HttpQueue queue = createQueue("popov");
        repository.deleteQueue("popov");
        assertThrows(QueueNotExistsException.class, () -> repository.pop(queue.getId()));
    }

    @Test
    void ignoreRecreatingAQueue() {
        HttpQueue queue = createQueue("popov");
        assertThat(createQueue("popov").getId()).isEqualTo(queue.getId());
    }

    @Test
    void popShouldClearQueue() {
        HttpQueue queue = createQueue("q-name");
        repository.updateQueueSchedule("q-name", SCHEDULE);
        repository.push(new TickMessage(SCHEDULE));
        assertThat(repository.pop(queue.getId())).hasSize(1);
        assertThat(repository.pop(queue.getId())).hasSize(0);
    }

    @Test
    void shouldBeFalseOnNonExistingQueue() {
        assertThat(repository.isQueueExists("no-name")).isFalse();
    }

    @Test
    void shouldAlterAssignedSchedule() {
        HttpQueue queue = createQueue("q-name");
        repository.updateQueueSchedule("q-name", "");
        repository.push(new TickMessage(SCHEDULE));
        assertThat(repository.pop(queue.getId())).isEmpty();
    }

    @Test
    @DisabledIfSystemProperty(named = "scope", matches = "core")
    @Tag("slow")
    void deleteQueueIfNotInUse() {
        createQueue("q-name");
        await().atMost(Duration.ONE_MINUTE).until(() -> !repository.isQueueExists("q-name"));
    }
}