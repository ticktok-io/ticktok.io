package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.IntegrationTest;
import test.io.ticktok.server.support.RepositoryCleanupExtension;

import java.time.Clock;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SchedulesRepositoryTestConfiguration.class, RepositoryCleanupExtension.class})
@IntegrationTest
class SchedulesRepositoryTest {

    static final int SECOND = 1000;
    static final io.ticktok.server.clock.Clock CLOCK_11 =
            new io.ticktok.server.clock.Clock("1423", "kuku11", "every.11.seconds");


    @Autowired
    @RegisterExtension
    RepositoryCleanupExtension repositoryCleanupExtension;
    @Autowired
    SchedulesRepository schedulesRepository;
    @Autowired
    Clock systemClock;

    @Test
    void addANewSchedule() {
        schedulesRepository.addClock(CLOCK_11);
        Schedule createdSchedule = schedulesRepository.findBySchedule("every.11.seconds").get();
        assertThat(createdSchedule.getNextTick(), is(systemClock.millis()));
        assertThat(createdSchedule.getClocks(), hasSize(1));
        assertThat(createdSchedule.getClocks(), contains(CLOCK_11.getId()));
    }

    @Test
    void increaseClockCountOnAddingExistingSchedule() {
        schedulesRepository.addClock(CLOCK_11);
        schedulesRepository.addClock(CLOCK_11);
        List<Schedule> allSchedules = schedulesRepository.findAll();
        assertThat(allSchedules.size(), is(1));
        assertThat(allSchedules.get(0).getClocks(), hasSize(1));
        assertThat(allSchedules.get(0).getClocks(), contains(CLOCK_11.getId()));
    }

    @Test
    void decreaseClockCount() {
        schedulesRepository.addClock(CLOCK_11);
        schedulesRepository.removeClock(CLOCK_11);
        Schedule schedule = schedulesRepository.findBySchedule("every.11.seconds").get();
        assertThat(schedule.getClocks(), not(contains(CLOCK_11.getId())));
    }

    @Test
    void findSchedulesWithNextTickUpUntilTime() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND, asList("111")));
        schedulesRepository.save(new Schedule("every.9.seconds", now() + SECOND, asList("111")));
        schedulesRepository.save(new Schedule("every.10.seconds", now() + 3000, asList("222")));
        List<Schedule> schedules = schedulesRepository.findActiveSchedulesByNextTickLesserThan(now() + 2000);
        assertThat(schedules.size(), is(2));
        assertThat(schedules.get(0).getSchedule(), is("every.8.seconds"));
        assertThat(schedules.get(1).getSchedule(), is("every.9.seconds"));
    }

    private long now() {
        return systemClock.millis();
    }

    @Test
    void shouldNotRetrieveNonActiveSchedules() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND));
        assertThat(schedulesRepository.findActiveSchedulesByNextTickLesserThan(now()).size(), is(0));
    }

    @Test
    void updateNextTick() {
        Schedule schedule = schedulesRepository.save(new Schedule("every.8.seconds", 1111));
        schedulesRepository.updateNextTick(schedule.getId(), 2222);
        assertThat(schedulesRepository.findById(schedule.getId()).get().getNextTick(), is(2222L));
    }

    @Test
    void deleteNonActiveClocks() {
        schedulesRepository.save(new Schedule("every.8.seconds", 0));
        schedulesRepository.save(new Schedule("every.7.seconds", 0, emptyList()));
        schedulesRepository.save(new Schedule("every.10.seconds", 0, asList("222")));
        schedulesRepository.deleteNonActiveSchedules();
        List<Schedule> leftSchedules = schedulesRepository.findAll();
        assertThat(leftSchedules, hasSize(1));
        assertThat(leftSchedules.get(0).getSchedule(), is("every.10.seconds"));

    }
}