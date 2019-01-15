package test.io.ticktok.server.clock.repository;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClocksFinderTest.ClocksFinderTestConfiguration.class, ApplicationConfig.class, RepositoryCleanupConfiguration.class})
class ClocksFinderTest {

    @Configuration
    @EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"})
    static class ClocksFinderTestConfiguration {
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
}