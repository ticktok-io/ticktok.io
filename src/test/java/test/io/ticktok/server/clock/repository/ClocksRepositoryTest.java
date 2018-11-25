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
import static org.hamcrest.Matchers.containsInAnyOrder;
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
        Clock savedClock = saveClock("kuku", "every.5.seconds");
        saveClock("kuku", "every.10.seconds");
        assertThat(repository.findAll(), hasSize(1));
        assertThat(repository.findById(savedClock.getId()).get().getSchedules(),
                containsInAnyOrder("every.10.seconds", "every.5.seconds"));
    }

    private Clock saveClock(String name, String... schedules) {
        Clock clock = null;
        for (String schedule : schedules) {
            clock = repository.saveClock(name, schedule);
        }
        return clock;
    }

    @Test
    void createScheduleOnNewClock() {
        saveClock("kuku", "every.3.seconds");
        verify(schedulesRepositoryMock).addClockFor("every.3.seconds");
    }

    @Test
    void findClocksWithRedundantSchedules() {
        saveClock("popov", "every.4.seconds", "every.5.seconds");
        saveClock("kuku", "every.4.seconds");
        List<Clock> schedules = repository.findByMoreThanOneSchedule();
        assertThat(schedules, hasSize(1));
        assertThat(schedules.get(0).getName(), is("popov"));
    }

    @Test
    void removeSchedule() {
        Clock clock = saveClock("popov", "every.6.seconds", "every.8.seconds");
        repository.deleteScheduleByIndex(clock.getId(), 0);
        Clock newClock = repository.findById(clock.getId()).get();
        assertThat(newClock.getSchedules(), hasSize(1));
        assertThat(newClock.getSchedules().get(0), is(clock.getSchedules().get(1)));
    }

    @Test
    void deleteAllClocksWithoutSchedules() {
        Clock popov = repository.saveClock("popov", "every.4.seconds");
        repository.saveClock("kuku", "every.4.seconds");
        repository.deleteScheduleByIndex(popov.getId(), 0);
        repository.deleteByNoSchedules();
        assertThat(repository.count(), is(1L));
        assertThat(repository.findAll().get(0).getName(), is("kuku"));

    }

    @AfterEach
    void clearRepository() {
        repository.deleteAll();
    }
}