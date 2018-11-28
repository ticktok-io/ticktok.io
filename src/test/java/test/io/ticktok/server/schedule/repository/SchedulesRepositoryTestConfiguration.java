package test.io.ticktok.server.schedule.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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
