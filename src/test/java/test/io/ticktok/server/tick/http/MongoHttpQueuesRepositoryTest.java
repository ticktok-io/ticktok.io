package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpQueue;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import io.ticktok.server.tick.http.HttpQueuesRepository.QueueNotExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static java.time.Duration.ofMinutes;
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
    public static final TickMessage TICK_MESSAGE = new TickMessage(SCHEDULE);
    private static final String QUEUE_NAME = "q-name";
    private HttpQueue queue;

    @Configuration
    @ComponentScan(basePackages = {"io.ticktok.server.tick.http"})
    static class TestConfiguration {

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    @Autowired
    private HttpQueuesRepository repository;

    @BeforeEach
    void setUp() {
        queue = repository.createQueue(QUEUE_NAME);
    }

    @AfterEach
    void tearDown() {
        repository.deleteQueue(QUEUE_NAME);
    }

    @Test
    void failOnNonExistingQueue() {
        assertThrows(QueueNotExistsException.class, () -> repository.pop("unknown-queue-id"));
    }

    @Test
    void createNewQueue() {
        assertThat(repository.isQueueExists(queue.getName())).isTrue();
        assertThat(repository.pop(queue.getExternalId())).isEmpty();
    }

    @Test
    void retrievePushedTicks() {
        repository.updateQueueSchedule(QUEUE_NAME, SCHEDULE);
        repository.push(TICK_MESSAGE);
        repository.push(TICK_MESSAGE);
        assertThat(repository.pop(queue.getExternalId())).containsExactly(
                new TickMessage(SCHEDULE), new TickMessage(SCHEDULE));
    }

    @Test
    void deleteQueue() {
        repository.deleteQueue(QUEUE_NAME);
        assertThrows(QueueNotExistsException.class, () -> repository.pop(queue.getExternalId()));
    }

    @Test
    void ignoreRecreatingAQueue() {
        assertThat(repository.createQueue(QUEUE_NAME).getId()).isEqualTo(queue.getId());
    }

    @Test
    void popShouldClearQueue() {
        repository.updateQueueSchedule(QUEUE_NAME, SCHEDULE);
        repository.push(new TickMessage(SCHEDULE));
        assertThat(repository.pop(queue.getExternalId())).hasSize(1);
        assertThat(repository.pop(queue.getExternalId())).hasSize(0);
    }

    @Test
    void shouldBeFalseOnNonExistingQueue() {
        assertThat(repository.isQueueExists("no-name")).isFalse();
    }

    @Test
    void shouldAlterAssignedSchedule() {
        repository.updateQueueSchedule(QUEUE_NAME, "");
        repository.push(TICK_MESSAGE);
        assertThat(repository.pop(queue.getExternalId())).isEmpty();
    }

    @Test
    @DisabledIfSystemProperty(named = "scope", matches = "core")
    @Tag("slow")
    void deleteQueueIfNotInUse() {
        await().atMost(ofMinutes(1)).until(() -> !repository.isQueueExists(QUEUE_NAME));
    }

    @Test
    void pushTickForASpecificQueue() {
        repository.push(QUEUE_NAME, TICK_MESSAGE);
        assertThat(repository.pop(queue.getExternalId())).contains(TICK_MESSAGE);
    }
}