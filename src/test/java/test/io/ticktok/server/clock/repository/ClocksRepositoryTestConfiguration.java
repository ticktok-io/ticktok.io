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
@ComponentScan(basePackages = {"io.ticktok.server.clock.repository"})
public class ClocksRepositoryTestConfiguration {

    @Bean
    @Primary
    public SchedulesRepository schedulesRepository() {
        return mock(SchedulesRepository.class);
    }
}
