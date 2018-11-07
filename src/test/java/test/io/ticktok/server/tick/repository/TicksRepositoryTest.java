package test.io.ticktok.server.tick.repository;

import io.ticktok.server.clock.Schedule;
import io.ticktok.server.tick.Tick;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class TicksRepositoryTest {

    public static final Tick TICK = Tick.create(new Schedule("c", "every.10.seconds", 0L, 1), 1234L);
    @Autowired
    TicksRepository repository;

    @Test
    void updateTickStatus() {
        Tick savedTick = repository.save(TICK);
        repository.updateTickStatus(savedTick.getId(), Tick.PUBLISHED);
        assertThat(repository.findById(savedTick.getId()).get().getStatus(), is(Tick.PUBLISHED));
    }

    @Test
    void failWhenUpdatingAnAlreadyExistStatus() {
        Tick savedTick = repository.save(TICK);
        assertThrows(TicksRepository.UnableToUpdateStatusException.class,
                () -> repository.updateTickStatus(savedTick.getId(), Tick.PENDING));
    }

    @AfterEach
    void clearDb() {
        repository.deleteAll();
    }
}