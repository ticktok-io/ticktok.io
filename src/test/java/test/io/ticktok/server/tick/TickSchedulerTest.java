package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.TickScheduler;
import io.ticktok.server.tick.TicksRepository;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class TickSchedulerTest {

    @Autowired
    ClocksRepository clocksRepository;
    @Autowired
    TicksRepository ticksRepository;

    @Test
    void updateNextTicks() {
        clocksRepository.save(new Clock("1", "every.2.seconds", 0L));
        clocksRepository.save(new Clock("2", "every.4.seconds", 0L));
        schedule();
        assertThat(clocksRepository.findById("1").get().getLatestScheduledTick(), is(2000L));
        assertThat(clocksRepository.findById("2").get().getLatestScheduledTick(), is(4000L));
    }

    private void schedule() {
        new TickScheduler(clocksRepository, ticksRepository).schedule();
    }

    @Test
    void ignoreClocksWithFutureScheduledTicks() {
        clocksRepository.save(new Clock("1", "every.2.seconds", System.currentTimeMillis() + 11 * 1000));
        schedule();
        assertThat(ticksRepository.findAll(), is(empty()));
    }

    @Test
    void scheduleNewTicks() {
        clocksRepository.save(new Clock("1", "every.2.seconds", 0L));
        clocksRepository.save(new Clock("2", "every.4.seconds", 0L));
        schedule();
        List<Tick> ticks = ticksRepository.findAll();
        assertThat(ticks.stream().filter(t -> t.getClockId().equals("1")).findFirst().get().getTime(), is(2000L));
        assertThat(ticks.stream().filter(t -> t.getClockId().equals("2")).findFirst().get().getTime(), is(4000L));
    }

    @AfterEach
    void tearDown() {
        clocksRepository.deleteAll();
        ticksRepository.deleteAll();
    }
}