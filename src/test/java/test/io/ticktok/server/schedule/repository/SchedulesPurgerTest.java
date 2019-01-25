package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.schedule.repository.SchedulesPurger;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SchedulesPurgerTest.SchedulesPurgerTestConfiguration.class})
class SchedulesPurgerTest {

    @Configuration
    @ComponentScan(basePackages = {"io.ticktok.server.schedule.repository"})
    static class SchedulesPurgerTestConfiguration {
        @Bean
        public SchedulesRepository schedulesRepository() {
            return mock(SchedulesRepository.class);
        }
    }

    @Autowired
    SchedulesRepository schedulesRepository;
    @Autowired
    SchedulesPurger schedulesPurger;

    @Test
    void deleteAllSchedulesWithNoClocks() {
        schedulesPurger.purge();
        verify(schedulesRepository).deleteNonActiveSchedules();
    }

}