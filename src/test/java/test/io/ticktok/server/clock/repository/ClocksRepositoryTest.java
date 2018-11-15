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

    /*@Test
    void createScheduleOnNewClock() {
        Clock clock = new Clock("kuku", "every.5.seconds");
        repository.saveClock(clock);
        verify(schedulesRepositoryMock).addClockFor(clock.getSchedules());
    }

    @Test
    void removeOldClockScheduleWhenUpdatingExistingClock() {
        Clock oldClock = new Clock("kuku", "every.5.seconds");
        Clock newClock = new Clock("kuku", "every.11.seconds");
        repository.saveClock(oldClock);
        repository.saveClock(newClock);
        verify(schedulesRepositoryMock).removeClockFor(oldClock.getSchedules());
        verify(schedulesRepositoryMock).addClockFor(newClock.getSchedules());
    }*/

    @AfterEach
    void clearRepository() {
        repository.deleteAll();
    }
}