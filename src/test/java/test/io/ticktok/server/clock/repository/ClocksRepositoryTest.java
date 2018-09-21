package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import static org.hamcrest.core.Is.is;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class ClocksRepositoryTest {

    @Autowired
    ClocksRepository repository;


    @Test
    void updateClockLatestScheduledTick() {
        Clock savedClock = repository.save(new Clock());
        repository.updateLatestScheduledTick(savedClock.getId(), 111222L);
        Assert.assertThat(repository.findById(savedClock.getId()).get().getLatestScheduledTick(), is(111222L));
    }

    @AfterEach
    void clearDb() {
        repository.deleteAll();
    }
}