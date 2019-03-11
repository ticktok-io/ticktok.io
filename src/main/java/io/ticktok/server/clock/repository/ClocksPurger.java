package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.logging.LogExecutionTime;
import io.ticktok.server.tick.TickChannelOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClocksPurger {

    private final ClocksRepository clocksRepository;
    private final TickChannelOperations tickChannelOperations;

    public ClocksPurger(ClocksRepository clocksRepository,
                        TickChannelOperations tickChannelOperations) {
        this.clocksRepository = clocksRepository;
        this.tickChannelOperations = tickChannelOperations;
    }

    @LogExecutionTime
    @Scheduled(initialDelay = 0, fixedDelayString = "${clocks.purge.interval}")
    public void purge() {
        clocksRepository.findByStatus(Clock.ACTIVE).forEach(this::deleteRedundantSchedules);
    }

    private void deleteRedundantSchedules(Clock clock) {
        if (isChannelNotExistsFor(clock)) {
            clocksRepository.deleteClock(clock);
            log.info("Purged the clock: {}", clock);
        }
    }

    private boolean isChannelNotExistsFor(Clock clock) {
        return !tickChannelOperations.isExists(clock);
    }

}
