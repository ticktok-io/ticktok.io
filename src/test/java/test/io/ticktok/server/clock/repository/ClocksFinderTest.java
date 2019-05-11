package test.io.ticktok.server.clock.repository;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksFinder;
import io.ticktok.server.clock.repository.ClocksFinder.ClockNotFoundException;
import io.ticktok.server.clock.repository.ClocksRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static io.ticktok.server.clock.repository.ClocksRepository.not;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class ClocksFinderTest {

    ClocksRepository repository = mock(ClocksRepository.class);
    ArgumentCaptor<Map<String, String>> filterParameters = ArgumentCaptor.forClass(Map.class);

    @Test
    void ignorePendingClocks() {
        new ClocksFinder(repository).find();
        verify(repository).findBy(filterParameters.capture());
        assertThat(filterParameters.getValue().get("status")).isEqualTo(not(Clock.PENDING));
    }

    @Test
    void failOnNonExistingClock() {
        when(repository.findById("non-existing-id")).thenReturn(Optional.empty());
        assertThrows(ClockNotFoundException.class,
                () -> new ClocksFinder(repository).findById("non-existing-id"));
    }

    @Test
    void delegateParameterMapToRepository() {
        findBy("name", "kuku");
        verify(repository).findBy(filterParameters.capture());
        assertThat(filterParameters.getValue().get("name")).isEqualTo("kuku");
    }

    private void findBy(String name, String kuku) {
        new ClocksFinder(repository, ImmutableMap.of(name, kuku)).find();
    }

    @Test
    void delegateResultFromRepository() {
        final Clock clock = Clock.builder().name("lala").build();
        when(repository.findBy(any())).thenReturn(asList(clock));
        assertThat(new ClocksFinder(repository).find()).containsOnly(clock);
    }

    @Test
    void delegateGivenStatusAsFilter() {
        findBy("status", Clock.PENDING);
        verify(repository).findBy(filterParameters.capture());
        assertThat(filterParameters.getValue().get("status")).isEqualTo(Clock.PENDING);
    }
}