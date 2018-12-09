package test.io.ticktok.server.tick.repository;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TicksRepositoryTest.TicksRepositoryTestConfiguration.class})
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

    @AfterEach
    void clearDb() {
        repository.deleteAll();
    }
}