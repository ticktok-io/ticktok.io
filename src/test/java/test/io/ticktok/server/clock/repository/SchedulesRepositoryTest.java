package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Schedule;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.MongoTestConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoTestConfiguration.class})
class SchedulesRepositoryTest {

    public static final int SECOND = 1000;
    @Autowired
    SchedulesRepository schedulesRepository;

    @BeforeEach
    void setUp() {
        clearDBs();
    }

    void clearDBs() {
        schedulesRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        clearDBs();
    }

    @Test
    void addANewSchedule() {
        schedulesRepository.addClockFor("every.11.seconds");
        Schedule createdSchedule = schedulesRepository.findBySchedule("every.11.seconds").get();
        assertThat(createdSchedule.getClockCount(), is(1));
        assertThat(createdSchedule.getLatestScheduledTick(), is(MongoTestConfiguration.FIXED_INSTANT.toEpochMilli()));
    }

    @Test
    void updateScheduleLatestScheduledTick() {
        schedulesRepository.addClockFor("every.30.seconds");
        String scheduleId = schedulesRepository.findBySchedule("every.30.seconds").get().getId();
        schedulesRepository.updateLatestScheduledTick(scheduleId, 111222L);
        assertThat(schedulesRepository.findById(scheduleId).get().getLatestScheduledTick(), is(111222L));
    }

    @Test
    void increaseClockCountOnAddingExistingSchedule() {
        schedulesRepository.addClockFor("every.11.seconds");
        schedulesRepository.addClockFor("every.11.seconds");
        List<Schedule> allSchedules = schedulesRepository.findAll();
        assertThat(allSchedules.size(), is(1));
        assertThat(allSchedules.get(0).getClockCount(), is(2));
    }

    @Test
    void decreaseClockCount() {
        schedulesRepository.addClockFor("every.11.seconds");
        schedulesRepository.addClockFor("every.20.seconds");
        schedulesRepository.removeClockFor("every.11.seconds", "every.20.seconds");
        assertThat(schedulesRepository.findBySchedule("every.11.seconds").get().getClockCount(), is(0));
        assertThat(schedulesRepository.findBySchedule("every.20.seconds").get().getClockCount(), is(0));
    }

    @Test
    void findByLatestScheduledTick() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND, 1));
        schedulesRepository.save(new Schedule("every.10.seconds", now() + 10 * SECOND, 1));
        List<Schedule> schedules = schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(0, now());
        assertThat(schedules.size(), is(1));
        assertThat(schedules.get(0).getSchedule(), is("every.8.seconds"));
    }

    @Test
    void shouldNotRetrieveNonActiveSchedules() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND, 0));
        assertThat(schedulesRepository.findByClockCountGreaterThanAndLatestScheduledTickLessThanEqual(0, now()).size(), is(0));
    }

    private long now() {
        return System.currentTimeMillis();
    }

}