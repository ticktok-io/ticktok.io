package test.io.ticktok.server.tick.repository;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
        assertThat(repository.findById(savedTick.getId()).get().getStatus()).isEqualTo(Tick.PUBLISHED);
    }

    @Test
    void failWhenUpdatingAnAlreadyExistStatus() {
        Tick savedTick = repository.save(TICK);
        assertThatExceptionOfType(TicksRepository.UnableToUpdateStatusException.class).isThrownBy(() -> repository.updateTickStatus(savedTick.getId(), Tick.PENDING));
    }

    @Test
    void keepNonPublishedTicks() {
        repository.save(new Tick(null, "every.1.seconds", 111, Tick.PENDING));
        repository.save(new Tick(null, "every.2.seconds", 111, Tick.IN_PROGRESS));
        repository.deletePublishedExceptLastPerSchedule(1);
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void keepOnlyLastXPublishedTicks() {
        Tick tick1 = new Tick(null, "every.1.seconds", 111, Tick.PUBLISHED);
        Tick tick2 = new Tick(null, "every.1.seconds", 222, Tick.PUBLISHED);
        Tick tick3 = new Tick(null, "every.1.seconds", 333, Tick.PUBLISHED);
        repository.saveAll(asList(tick1, tick2, tick3));
        repository.deletePublishedExceptLastPerSchedule(2);
        assertThat(repository.findAll()).usingElementComparatorIgnoringFields("id").doesNotContain(tick1);
        repository.deletePublishedExceptLastPerSchedule(1);
        assertThat(repository.findAll()).usingElementComparatorIgnoringFields("id").containsOnly(tick3);
    }

    @Test
    void keepTicksPerSchedule() {
        Tick tick1 = new Tick(null, "every.1.seconds", 111, Tick.PUBLISHED);
        Tick tick12 = new Tick(null, "every.1.seconds", 122, Tick.PUBLISHED);
        Tick tick2 = new Tick(null, "every.2.seconds", 222, Tick.PUBLISHED);
        repository.saveAll(asList(tick1, tick12, tick2));
        repository.deletePublishedExceptLastPerSchedule(1);
        assertThat(repository.findAll()).usingElementComparatorIgnoringFields("id").containsOnly(tick12, tick2);
    }
}