package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class ClocksRepositoryTest {

    @Autowired
    ClocksRepository repository;
    @Autowired
    SchedulesRepository schedulesRepository;

    @Test
    void updateClockIfAlreadyExistsByName() {
        Clock savedClock = repository.saveClock(new Clock("kuku", "every.5.seconds"));
        repository.saveClock(new Clock("kuku", "every.10.seconds"));
        assertThat(repository.findAll(), hasSize(1));
        assertThat(repository.findById(savedClock.getId()).get().getSchedule(), is("every.10.seconds"));
    }

    @Test
    void increaseClokCountOnNewClock() {
        Clock savedClock = repository.saveClock(new Clock("kuku", "every.5.seconds"));
        assertThat(schedulesRepository.findBySchedule(savedClock.getSchedule()).get().getClockCount(), is(1));
    }

    @AfterEach
    void clearRepository() {
        repository.deleteAll();
    }
}