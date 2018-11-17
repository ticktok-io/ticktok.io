package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.ClocksRepositoryListener;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.bson.Document;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static org.mockito.Mockito.*;

class ClocksRepositoryListenerTest {

    SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    ClocksRepository clocksRepository = mock(ClocksRepository.class);
    ClocksRepositoryListener listener =
            new ClocksRepositoryListener(schedulesRepository, clocksRepository);

    @Test
    void ignoreIfClockNotFound() {
        givenTheClocks();
        onBeforeDelete("4365");
        verifyZeroInteractions(schedulesRepository);
    }

    private void givenTheClocks(Clock... clocks) {
        mockFindByIdFor(clocks);
    }

    private void mockFindByIdFor(Clock[] clocks) {
        when(clocksRepository.findById(any())).thenReturn(Optional.empty());
        for (Clock clock : clocks) {
            when(clocksRepository.findById(clock.getId())).thenReturn(Optional.of(clock));
        }
    }

    private void onBeforeDelete(String clockId) {
        listener.onBeforeDelete(new BeforeDeleteEvent<>(new Document("_id", clockId), Clock.class, "clock"));
    }

}