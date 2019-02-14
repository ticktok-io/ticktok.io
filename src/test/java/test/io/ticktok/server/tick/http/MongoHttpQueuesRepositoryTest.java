package test.io.ticktok.server.tick.http;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickMessage;
import io.ticktok.server.tick.http.HttpConfiguration;
import io.ticktok.server.tick.http.HttpQueuesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private HttpQueuesRepository repository;

    @BeforeEach
    void setUp() {
        repository.assignClock(CLOCK_1_ID, SCHEDULE);
    }

    @Test
    void noAvailableTicks() {
        assertThat(repository.pop("non-id")).isEmpty();
    }

    @Test
    void retrieveTicksForClock() {
        repository.add(SCHEDULE);
        repository.add(SCHEDULE);
        List<TickMessage> ticks = repository.pop(CLOCK_1_ID);
        assertThat(ticks).hasSize(2);
        assertThat(ticks).containsOnly(new TickMessage(SCHEDULE));
    }

    /*@RepeatedTest(20)
    void deleteTickOnPop() throws InterruptedException, ExecutionException {
        repository.add(SCHEDULE);

        //assertThat(repository.pop(CLOCK_1_ID)).hasSize(1);
        //assertThat(repository.pop(CLOCK_1_ID)).isEmpty();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Set<Callable<Integer>> actions = new HashSet<>(asList(
                () -> repository.pop(CLOCK_1_ID).size(),
                () -> repository.pop(CLOCK_1_ID).size()));

        List<Future<Integer>> result = executorService.invokeAll(actions);

        assert result.stream().map(integerFuture -> {
            try {
                return integerFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                //ignore
            }
            return -1;
        }).collect(Collectors.toList()).contains(1);

        assert result.stream().map(integerFuture -> {
            try {
                return integerFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                //ignore
            }
            return -1;
        }).collect(Collectors.toList()).contains(0);

    }

    @Test
    void retrieveSameTickByMultipleClocks() throws InterruptedException, ExecutionException {
        repository.assignClock(CLOCK_2_ID, SCHEDULE);
        repository.add(SCHEDULE);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Set<Callable<Boolean>> actions = new HashSet<>(asList(
                () -> repository.pop(CLOCK_1_ID).size() == 1,
                () -> repository.pop(CLOCK_2_ID).size() == 1));

        List<Future<Boolean>> result = executorService.invokeAll(actions);
        for(Future<Boolean> f : result) {
            assertThat(f.get()).isTrue();
        }
    }*/

}