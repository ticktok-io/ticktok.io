package test.io.ticktok.server.clock;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ClockActivatorAspect;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ClockActivatorAspectTest.TestConfiguration.class})
@SpringBootTest
class ClockActivatorAspectTest {

    @Configuration
    @ComponentScan(basePackageClasses = {ClockActivatorAspect.class},
            includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClockActivatorAspect.class )},
            useDefaultFilters = false)
    @EnableAspectJAutoProxy
    static class TestConfiguration {

        @Bean
        public TickChannelExplorer tickChannelExplorer() {
            return mock(TickChannelExplorer.class);
        }

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    @Autowired
    TickChannelExplorer tickChannelExplorer;
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
        tickChannelExplorer.create(clock);
        verify(clocksRepository).updateStatus(clock.getId(), Clock.ACTIVE);
    }

}