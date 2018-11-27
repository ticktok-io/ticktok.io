package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.mock;

@Configuration
@EnableMongoRepositories(basePackages = {"io.ticktok.server.schedule.repository"})
@ComponentScan(basePackages = {"io.ticktok.server.schedule.repository"})
public class SchedulesRepositoryTestConfiguration {


    public static final Instant INSTANT = Instant.parse("2018-02-01T10:15:00.00Z");

    @Bean
    public Clock systemClock() {
        return Clock.fixed(INSTANT, ZoneId.of("UTC"));
    }
}
