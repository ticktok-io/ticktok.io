package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SchedulesRepositoryTestConfiguration.class})
class SchedulesRepositoryTest {

    static final int SECOND = 1000;

    @Autowired
    SchedulesRepository schedulesRepository;
    @Autowired
    Clock systemClock;

    @BeforeEach
    void setUp() {
        clearDBs();
    }

    void clearDBs() {
        schedulesRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        //clearDBs();
    }

    @Test
    void addANewSchedule() {
        schedulesRepository.addSchedule("every.11.seconds");
        Schedule createdSchedule = schedulesRepository.findBySchedule("every.11.seconds").get();
        assertThat(createdSchedule.getClockCount(), is(1));
        assertThat(createdSchedule.getNextTick(), is(systemClock.millis()));
    }

    @Test
    void increaseClockCountOnAddingExistingSchedule() {
        schedulesRepository.addSchedule("every.11.seconds");
        schedulesRepository.addSchedule("every.11.seconds");
        List<Schedule> allSchedules = schedulesRepository.findAll();
        assertThat(allSchedules.size(), is(1));
        assertThat(allSchedules.get(0).getClockCount(), is(2));
    }

    @Test
    void decreaseClockCount() {
        schedulesRepository.addSchedule("every.11.seconds");
        schedulesRepository.removeSchedule("every.11.seconds");
        assertThat(schedulesRepository.findBySchedule("every.11.seconds").get().getClockCount(), is(0));
    }

    @Test
    void findByLatestScheduledTick() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND, 1));
        schedulesRepository.save(new Schedule("every.10.seconds", now() + 10 * SECOND, 1));
        List<Schedule> schedules = schedulesRepository.findByClockCountGreaterThanAndNextTickLessThanEqual(0, now());
        assertThat(schedules.size(), is(1));
        assertThat(schedules.get(0).getSchedule(), is("every.8.seconds"));
    }

    private long now() {
        return System.currentTimeMillis();
    }

    @Test
    void shouldNotRetrieveNonActiveSchedules() {
        schedulesRepository.save(new Schedule("every.8.seconds", now() - SECOND, 0));
        assertThat(schedulesRepository.findByClockCountGreaterThanAndNextTickLessThanEqual(0, now()).size(), is(0));
    }

    @Test
    void updateNextTick() {
        Schedule schedule = schedulesRepository.save(new Schedule("every.8.seconds", 1111, 1));
        schedulesRepository.updateNextTick(schedule.getId(), 2222);
        assertThat(schedulesRepository.findById(schedule.getId()).get().getNextTick(), is(2222L));
    }

    @Test
    void name() throws InterruptedException {
        List<Callable<Boolean>> newClocks = asList(
                () -> {
                    schedulesRepository.addSchedule("every.4.seconds");
                    return true;

                },
                () -> {
                    schedulesRepository.addSchedule("every.4.seconds");
                    return true;

                },
                () -> {
                    schedulesRepository.addSchedule("every.4.seconds");
                    return true;

                }
        );

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        executorService.invokeAll(newClocks).stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                Assert.fail();
            }
            return null;
        }).forEach(Assertions::assertTrue);
        executorService.shutdownNow();

    }
}