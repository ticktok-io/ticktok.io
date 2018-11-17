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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClocksRepositoryTestConfiguration.class})
class ClocksRepositoryTest {

    @Autowired
    ClocksRepository repository;
    @Autowired
    SchedulesRepository schedulesRepositoryMock;

    @Test
    void updateClockIfAlreadyExistsByName() {
        Clock savedClock = repository.saveClock("kuku", "every.5.seconds");
        repository.saveClock("kuku", "every.10.seconds");
        assertThat(repository.findAll(), hasSize(1));
        assertThat(repository.findById(savedClock.getId()).get().getSchedule(), is("every.10.seconds"));
    }

    @Test
    void createScheduleOnNewClock() {
        repository.saveClock("kuku", "every.3.seconds");
        verify(schedulesRepositoryMock).addClockFor("every.3.seconds");
    }

    @Test
    void removeClockSchedulesOnClockDelete() {
        repository.saveClock("kuku", "every.4.seconds");
        Clock savedClock = repository.saveClock("kuku", "every.8.seconds");
        repository.deleteById(savedClock.getId());
        verify(schedulesRepositoryMock).removeClockFor(savedClock.getSchedules().toArray(new String[0]));
    }

    @Test
    void findClocksWithRedundantSchedules() {
        repository.saveClock("popov", "every.4.seconds");
        repository.saveClock("popov", "every.5.seconds");
        repository.saveClock("kuku", "every.4.seconds");
        List<Clock> schedules = repository.findByMoreThanOneSchedule();
        assertThat(schedules, hasSize(1));
        assertThat(schedules.get(0).getName(), is("popov"));
    }

    @AfterEach
    void clearRepository() {
        repository.deleteAll();
    }
}