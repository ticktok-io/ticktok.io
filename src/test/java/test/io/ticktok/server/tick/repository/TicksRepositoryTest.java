package test.io.ticktok.server.tick.repository;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.repository.TicksRepository;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.RepositoryCleanupConfiguration;
import test.io.ticktok.server.support.RepositoryCleanupExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.refEq;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TicksRepositoryTest.TicksRepositoryTestConfiguration.class, RepositoryCleanupConfiguration.class})
class TicksRepositoryTest {

    @Configuration
    @EnableMongoRepositories({"io.ticktok.server.tick.repository"})
    static class TicksRepositoryTestConfiguration {
        public static final Instant FIXED_INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");

        @Bean
        public Clock systemClock() {
            return Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));
        }
    }

    @Autowired
    @RegisterExtension
    RepositoryCleanupExtension repositoryCleanupExtension;


    public static final Tick TICK = Tick.create(new Schedule("every.10.seconds", 1234L));
    @Autowired
    TicksRepository repository;

    @Test
    void updateTickStatus() {
        Tick savedTick = repository.save(TICK);
        repository.updateTickStatus(savedTick.getId(), Tick.PUBLISHED);
        assertThat(repository.findById(savedTick.getId()).get().getStatus(), is(Tick.PUBLISHED));
    }

    @Test
    void failWhenUpdatingAnAlreadyExistStatus() {
        Tick savedTick = repository.save(TICK);
        assertThrows(TicksRepository.UnableToUpdateStatusException.class,
                () -> repository.updateTickStatus(savedTick.getId(), Tick.PENDING));
    }

    @Test
    void keepNonPublishedTicks() {
        repository.save(new Tick(null, "every.1.seconds", 111, Tick.PENDING));
        repository.save(new Tick(null, "every.2.seconds", 111, Tick.IN_PROGRESS));
        repository.deletePublishedExceptLastPerSchedule(1);
        assertThat(repository.findAll(), hasSize(2));
    }

    @Test
    void keepOnlyLastXPublishedTicks() {
        Tick tick1 = new Tick(null, "every.1.seconds", 111, Tick.PUBLISHED);
        Tick tick2 = new Tick(null, "every.1.seconds", 222, Tick.PUBLISHED);
        Tick tick3 = new Tick(null, "every.1.seconds", 333, Tick.PUBLISHED);
        repository.saveAll(asList(tick1, tick2, tick3));
        repository.deletePublishedExceptLastPerSchedule(2);
        Assertions.assertThat(repository.findAll()).usingElementComparatorIgnoringFields("id").doesNotContain(tick3);
        repository.deletePublishedExceptLastPerSchedule(1);
        Assertions.assertThat(repository.findAll()).usingElementComparatorIgnoringFields("id").containsOnly(tick1);
    }
}