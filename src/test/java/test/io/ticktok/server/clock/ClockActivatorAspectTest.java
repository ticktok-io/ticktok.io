package test.io.ticktok.server.clock;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClockActivatorAspect;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClockActivatorAspectTest.TestConfiguration.class})
@SpringBootTest
class ClockActivatorAspectTest {

    @Configuration
    @ComponentScan(basePackageClasses = {ClockActivatorAspect.class},
            /*excludeFilters = {@ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "io.ticktok.server.clock.*")},*/
            includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClockActivatorAspect.class )},
            useDefaultFilters = false)
    @EnableAspectJAutoProxy
    static class TestConfiguration {

        @Bean
        public TickChannelCreator tickChannelCreator() {
            return mock(TickChannelCreator.class);
        }

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    @Autowired
    TickChannelCreator tickChannelCreator;
    @Autowired
    ClocksRepository clocksRepository;


    @Test
    void activateClock() {
        Clock clock = Clock.builder()
                .id("1423")
                .name("aspect-kuku")
                .schedule("every.1.seconds")
                .status(Clock.PENDING)
                .build();
        when(clocksRepository.findByNameAndSchedule(clock.getName(), clock.getSchedule())).thenReturn(Optional.of(clock));
        tickChannelCreator.create(clock.getName(), clock.getSchedule());
        verify(clocksRepository).updateStatus(clock.getId(), Clock.ACTIVE);
    }

    @Test
    void ignoreNonExistingClock() {
        when(clocksRepository.findByNameAndSchedule(any(), any())).thenReturn(Optional.empty());
        tickChannelCreator.create("kuku", "never");
        verify(clocksRepository, times(0)).updateStatus(any(), any());
    }
}