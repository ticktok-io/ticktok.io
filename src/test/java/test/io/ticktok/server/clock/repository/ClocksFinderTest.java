package test.io.ticktok.server.clock.repository;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksFinder;
import io.ticktok.server.clock.repository.ClocksFinder.ClockNotFoundException;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.config.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.support.RepositoryCleanupConfiguration;
import test.io.ticktok.server.support.RepositoryCleanupExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.ticktok.server.clock.repository.ClocksRepository.not;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClocksFinderTest.TestConfiguration.class, ApplicationConfig.class, RepositoryCleanupConfiguration.class})
class ClocksFinderTest {

    @Configuration
    @EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"})
    static class TestConfiguration {
    }

    @Autowired
    @RegisterExtension
    RepositoryCleanupExtension repositoryCleanupExtension;

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
        new ClocksFinder(repository, ImmutableMap.of("name", "kuku")).find();
        verify(repository).findBy(filterParameters.capture());
        assertThat(filterParameters.getValue().get("name")).isEqualTo("kuku");
    }

    @Test
    void delegateResultFromRepository() {
        final Clock clock = Clock.builder().name("lala").build();
        when(repository.findBy(any())).thenReturn(asList(clock));
        assertThat(new ClocksFinder(repository).find()).containsOnly(clock);
    }
}