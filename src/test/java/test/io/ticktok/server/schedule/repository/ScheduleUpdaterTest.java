package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;

import static org.mockito.Mockito.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ScheduleUpdaterTest.TestConfiguration.class})
class ScheduleUpdaterTest {

    @Configuration
    @EnableMongoRepositories(basePackages = {"io.ticktok.server.clock.repository"})
    @ComponentScan(basePackages = {"io.ticktok.server.clock.repository", "io.ticktok.server.schedule.repository"},
            excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SchedulesRepository.class))
    static class TestConfiguration {


        public static final Instant INSTANT = Instant.parse("2018-01-01T10:15:00.00Z");

        @Bean
        public Clock systemClock() {
            return Clock.systemUTC();
        }

        @Bean
        @Primary
        public SchedulesRepository schedulesRepository() {
            return mock(SchedulesRepository.class);
        }
    }

    @Autowired
    ClocksRepository clocksRepository;
    @Autowired
    SchedulesRepository schedulesRepository;

    @BeforeEach
    void setUp() {
        clocksRepository.deleteAll();
    }

    @Test
    void addScheduleUponNewClock() {
        clocksRepository.saveClock("kuku", "every.2.seconds");
        clocksRepository.saveClock("kuku", "every.2.seconds");
        verify(schedulesRepository, times(1)).addSchedule("every.2.seconds");
    }
}