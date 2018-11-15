package test.io.ticktok.server.clock.repository;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.mock;

@Configuration
@EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SchedulesRepository.class)})
public class ClocksRepositoryTestConfiguration {

    public static final Instant FIXED_INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");

    @Bean
    public Clock systemClock() {
        return Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));
    }

    @Bean
    @Primary
    public SchedulesRepository schedulesRepository() {
        return mock(SchedulesRepository.class);
    }
}
