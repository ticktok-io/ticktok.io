package test.io.ticktok.server.clock.repository;

import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.mock;

@Configuration
@EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"})
@ComponentScan(basePackages = {"io.ticktok.server.clock.repository", "io.ticktok.server.schedule.repository"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SchedulesRepository.class))
public class ClocksRepositoryTestConfiguration {


    public static final Instant INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");

    @Bean
    @Scope("prototype")
    public Clock systemClock() {
        return Clock.fixed(INSTANT, ZoneId.of("UTC"));
    }

    @Bean
    @Primary
    public SchedulesRepository schedulesRepository() {
        return mock(SchedulesRepository.class);
    }
}
