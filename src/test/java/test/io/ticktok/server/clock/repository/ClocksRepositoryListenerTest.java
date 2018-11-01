package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.ClocksRepositoryListener;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static org.mockito.Mockito.*;

class ClocksRepositoryListenerTest {

    static final Instant FIXED_INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");
    static final java.time.Clock FIXED_CLOCK = java.time.Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));

    SchedulesRepository schedulesRepository = mock(SchedulesRepository.class);
    ClocksRepository clocksRepository = mock(ClocksRepository.class);
    ClocksRepositoryListener listener =
            new ClocksRepositoryListener(schedulesRepository, clocksRepository, FIXED_CLOCK);

    @Test
    void deleteSchedule() {
        Clock clock = new Clock("1324", "kuku", "every.44.seconds");
        givenTheClocks(clock);
        onBeforeDelete(clock.getId());
        verify(schedulesRepository).deleteBySchedule(clock.getSchedule());
    }

    private void givenTheClocks(Clock... clocks) {
        mockFindByIdFor(clocks);
        mockCountByScheduleFor(clocks);
    }

    private void mockFindByIdFor(Clock[] clocks) {
        when(clocksRepository.findById(any())).thenReturn(Optional.empty());
        for (Clock clock : clocks) {
            when(clocksRepository.findById(clock.getId())).thenReturn(Optional.of(clock));
        }
    }

    private void mockCountByScheduleFor(Clock[] clocks) {
        when(clocksRepository.countBySchedule(any())).thenReturn(0);
        Arrays.stream(clocks).collect(groupingBy(Clock::getSchedule)).forEach((k, v) ->
                when(clocksRepository.countBySchedule(k)).thenReturn(v.size()));
    }

    private void onBeforeDelete(String clockId) {
        listener.onBeforeDelete(new BeforeDeleteEvent<>(new Document("_id", clockId), Clock.class, "clock"));
    }

    @Test
    void ignoreIfClockNotFound() {
        givenTheClocks();
        onBeforeDelete("4365");
        verifyZeroInteractions(schedulesRepository);
    }

    @Test
    void keepScheduleIfThereAreStillClocksDefinedForIt() {
        givenTheClocks(
                new Clock("111", "kuku", "every.44.seconds"),
                new Clock("233", "popov", "every.44.seconds"));
        onBeforeDelete("111");
        verifyZeroInteractions(schedulesRepository);
    }

}