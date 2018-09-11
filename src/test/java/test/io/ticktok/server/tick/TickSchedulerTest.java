package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClocksRepository;
import io.ticktok.server.tick.TickScheduler;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class TickSchedulerTest {

    @Autowired
    ClocksRepository clocksRepository;

    @Test
    void updateNextTick() {
        clocksRepository.save(new Clock("1", "every.2.seconds", 0L));
        clocksRepository.save(new Clock("2", "every.4.seconds", 0L));
        new TickScheduler(clocksRepository).schedule();
        assertThat(clocksRepository.findById("1").get().getLatestScheduledTick(), is(2000L));
        assertThat(clocksRepository.findById("2").get().getLatestScheduledTick(), is(4000L));
    }



}