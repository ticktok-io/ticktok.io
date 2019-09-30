package test.io.ticktok.server.clock.actions;


import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.actions.ClockActionFactory;
import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.TickPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {ClockActionTest.TestConfiguration.class})
public class ClockActionTest {

    @Configuration
    @ComponentScan(basePackages = "io.ticktok.server.clock.actions")
    static class TestConfiguration {

        @Bean
        public ClocksRepository clocksRepository() {
            return mock(ClocksRepository.class);
        }

        @Bean
        public TickChannelOperations tickChannelExplorer() {
            return mock(TickChannelOperations.class);
        }

        @Bean
        public TickPublisher tickPublisher() {
            return mock(TickPublisher.class);
        }

    }
    public static final Clock CLOCK = Clock.builder()
            .name("lulu")
            .schedule("every.12.seconds")
            .build();

    @Autowired
    ClockActionFactory clockActionFactory;
    @Autowired
    ClocksRepository clocksRepository;
    @Autowired
    TickChannelOperations tickChannelOperations;
    @Autowired
    TickPublisher tickPublisher;


    @BeforeEach
    public void findClockMock() {
        when(clocksRepository.findById(CLOCK.getId())).thenReturn(Optional.of(CLOCK));
    }


    @Test
    void failOnNonExistingAction() {
        Assertions.assertThrows(ClockActionFactory.ActionNotFoundException.class,
                () -> clockActionFactory.run("non-action", "123"));
    }
    @Nested
    class ResumeClockActionTest {
        @Test
        void shouldEnableClock() {
            clockActionFactory.run("resume", CLOCK.getId());
            verify(tickChannelOperations).enable(CLOCK);
        }

        @Test
        void shouldNotBeAvailableWhenClockIsActive() {
            final Clock activeClock = Clock.builder().status(Clock.ACTIVE).build();
            assertThat(clockActionFactory.availableActionsFor(activeClock)).doesNotContain("resume");
        }

        @Test
        void souldBeAvailableWhenClockIsPaused() {
            final Clock pausedClock = Clock.builder().status(Clock.PAUSED).build();
            assertThat(clockActionFactory.availableActionsFor(pausedClock)).contains("resume");
        }
    }
    @Nested
    class PauseClockActionTest {
        @Test
        void invokeDisableClock() {
            clockActionFactory.run("pause", CLOCK.getId());
            verify(tickChannelOperations).disable(CLOCK);
        }

    }
    @Nested
    class TickClockActionTest {

        @Test
        void manuallyTickSpecificClock() {
            clockActionFactory.run("tick", CLOCK.getId());
            verify(tickPublisher).publishForClock(CLOCK);
        }
    }

}
