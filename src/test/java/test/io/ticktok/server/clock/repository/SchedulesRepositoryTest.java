package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class SchedulesRepositoryTest {

    @Autowired
    ClocksRepository clocksRepository;
    @Autowired
    SchedulesRepository schedulesRepository;

    @Test
    void createNewScheduleUponNewClock() {
        clocksRepository.save(new Clock(null, "kuku", "kuku.schedule"));
        Schedule createdSchedule = schedulesRepository.findAll().get(0);
        assertThat(createdSchedule.getSchedule(), is("kuku.schedule"));
        assertThat(createdSchedule.getLatestScheduledTick(), is(SpringMongoConfiguration.FIXED_INSTANT.toEpochMilli()));
    }

    @Test
    void updateScheduleLatestScheduledTick() {
        Schedule savedSchedule = schedulesRepository.save(new Schedule());
        schedulesRepository.updateLatestScheduledTick(savedSchedule.getId(), 111222L);
        Assert.assertThat(schedulesRepository.findById(savedSchedule.getId()).get().getLatestScheduledTick(), is(111222L));
    }

    @Test
    void ignoreDuplicateSchedule() {
        clocksRepository.save(new Clock(null, "kuku", "kuku.schedule"));
        clocksRepository.save(new Clock(null, "popov", "kuku.schedule"));
        assertThat(schedulesRepository.findAll().size(), is(1));
    }

    @Test
    void deleteUnneededSchedules() {
        clocksRepository.save(new Clock("1", "popov", "popov.schedule"));
        clocksRepository.deleteById("1");
        assertThat(schedulesRepository.findAll(), is(emptyList()));
    }

    @AfterEach
    void clearDBs() {
        clocksRepository.deleteAll();
        schedulesRepository.deleteAll();
    }

}