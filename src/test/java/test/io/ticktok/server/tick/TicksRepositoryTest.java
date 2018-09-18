package test.io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class TicksRepositoryTest {

    @Autowired
    TicksRepository ticksRepository;

    @Test
    void updateTickStatus() {
        Tick savedTick = ticksRepository.save(Tick.create(new Clock("23", "every.10.seconds"), 1234L));
        ticksRepository.updateTickStatus(savedTick.getId(), Tick.PUBLISHED);
        assertThat(ticksRepository.findById(savedTick.getId()).get().getStatus(), is(Tick.PUBLISHED));
    }

    @Test
    void failWhenUpdatingAnAlreadyExistStatus() {
        Tick savedTick = ticksRepository.save(Tick.create(new Clock("23", "every.10.seconds"), 1234L));
        assertThrows(TicksRepository.UnableToUpdateStatusException.class,
                () -> ticksRepository.updateTickStatus(savedTick.getId(), Tick.PENDING));
    }
}