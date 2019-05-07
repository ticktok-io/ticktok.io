package test.io.ticktok.server.clock.repository;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksFinder;
import io.ticktok.server.clock.repository.ClocksFinder.ClockNotFoundException;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.config.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.RepositoryCleanupConfiguration;
import test.io.ticktok.server.support.RepositoryCleanupExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClocksFinderTest.TestConfiguration.class, ApplicationConfig.class, RepositoryCleanupConfiguration.class})
class ClocksFinderTest {

    @Configuration
    @EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"})
    static class TestConfiguration {
    }

    @Autowired
    @RegisterExtension
    RepositoryCleanupExtension repositoryCleanupExtension;

    @Autowired
    ClocksRepository repository;

    @Test
    void ignorePendingClocks() {
        repository.save(Clock.builder().name("aaa").schedule("every.X.seconds").status(Clock.PENDING).build());
        assertTrue(new ClocksFinder(repository).find().isEmpty());
    }

    @Test
    void failOnNonExistingClock() {
        assertThrows(ClockNotFoundException.class,
                () -> new ClocksFinder(repository).findById("non-existing-id"));
    }

    @Test
    void findByName() {
        List<Clock> clocks = asList(
                repository.save(Clock.builder().name("kuku").schedule("every.1.seconds").status(Clock.ACTIVE).build()),
                repository.save(Clock.builder().name("kuku").schedule("every.12.seconds").status(Clock.ACTIVE).build()),
                repository.save(Clock.builder().name("popo").schedule("every.123.seconds").status(Clock.ACTIVE).build()));
        List<Clock> result = new ClocksFinder(repository, ImmutableMap.of("name", "kuku")).find();
        assertThat(result).hasSize(2);
        assertThat(result).containsOnlyOnce(clocks.get(0), clocks.get(1));
    }
}