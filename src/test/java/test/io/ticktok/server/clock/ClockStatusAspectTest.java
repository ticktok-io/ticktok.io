package test.io.ticktok.server.clock;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.repository.ClockStatusAspect;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
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
@ContextConfiguration(classes = {ClockStatusAspectTest.TestConfiguration.class})
@SpringBootTest
class ClockStatusAspectTest {

    @Configuration
    @ComponentScan(basePackageClasses = {ClockStatusAspect.class},
            includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ClockStatusAspect.class )},
            useDefaultFilters = false)
    @EnableAspectJAutoProxy
    static class TestConfiguration {

        @Bean
        public TickChannelOperations tickChannelExplorer() {
            return mock(TickChannelOperations.class);
        }

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }
    }

    public static final Clock CLOCK = Clock.builder()
            .id("1423")
            .name("aspect-kuku")
            .schedule("every.1.seconds")
            .status(Clock.PENDING)
            .build();

    @Autowired
    TickChannelOperations tickChannelOperations;
    @Autowired
    ClocksRepository clocksRepository;

    @BeforeEach
    public void resetMocks() {
        reset(clocksRepository, tickChannelOperations);
    }

    @Test
    void pauseClockAfterClockDisable() {
        tickChannelOperations.disable(CLOCK);
        verify(clocksRepository).updateStatus(CLOCK.getId(), Clock.PAUSED);
    }

    @Test
    void activateClockAfterClockEnable() {
        tickChannelOperations.enable(CLOCK);
        verify(clocksRepository).updateStatus(CLOCK.getId(), Clock.ACTIVE);
    }
}