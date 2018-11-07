package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.Schedule;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class SchedulesRepositoryTest {

    public static final int SECOND = 1000;
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
        assertThat(schedulesRepository.findById(savedSchedule.getId()).get().getLatestScheduledTick(), is(111222L));
    }

    @Test
    void ignoreDuplicateSchedule() {
        clocksRepository.save(new Clock(null, "kuku", "kuku.schedule"));
        clocksRepository.save(new Clock(null, "popov", "kuku.schedule"));
        assertThat(schedulesRepository.findAll().size(), is(1));
    }

    @Test
    void decreaseClockCountOnClockDelete() {
        Clock clock = new Clock("1", "popov", "popov.schedule");
        clocksRepository.save(clock);
        clocksRepository.deleteById(clock.getId());
        assertThat(findScheduleBy(clock.getSchedule()).getClockCount(), is(0));
    }

    @NotNull
    private Schedule findScheduleBy(String schedule) {
        return schedulesRepository.findAll().stream()
                .filter(s -> s.getSchedule().equals(schedule))
                .findFirst()
                .get();
    }

    @Test
    void increaseClockCount() {
        Schedule entity = new Schedule("2", "every.11.seconds", 0L, 2);
        schedulesRepository.save(entity);
        schedulesRepository.increaseClockCount(entity.getSchedule());
        assertThat(schedulesRepository.findById(entity.getId()).get().getClockCount(), is(3));
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

    @AfterEach
    void clearDBs() {
        clocksRepository.deleteAll();
        schedulesRepository.deleteAll();
    }

}