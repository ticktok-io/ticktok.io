package test.io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.schedule.repository.ScheduleUpdaterAspect;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ScheduleUpdaterAspectTest.TestConfiguration.class})
@SpringBootTest
class ScheduleUpdaterAspectTest {


    @Configuration
    @ComponentScan(basePackageClasses = {ScheduleUpdaterAspect.class},
            includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ScheduleUpdaterAspect.class )},
            useDefaultFilters = false)
    @EnableAspectJAutoProxy
    static class TestConfiguration {

        @Bean
        public SchedulesRepository schedulesRepository() {
            return mock(SchedulesRepository.class);
        }

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    @Autowired ClocksRepository clocksRepository;
    @Autowired SchedulesRepository schedulesRepository;

    @BeforeEach
    void resetMocks() {
        reset(schedulesRepository);
    }

    @Test
    void addScheduleOnClockSave() {
        clocksRepository.saveClock("kuku", "every.3.seconds");
        verify(schedulesRepository).addSchedule("every.3.seconds");
    }

    @Test
    void removeScheduleOnClockDelete() {
        Clock clock11 = Clock.builder().schedule("every.11.seconds").build();
        Clock clock9 = Clock.builder().schedule("every.9.seconds").build();
        clocksRepository.deleteClock(clock11);
        clocksRepository.deleteClock(clock9);
        verify(schedulesRepository).removeSchedule(clock11.getSchedule());
        verify(schedulesRepository).removeSchedule(clock9.getSchedule());
    }
}