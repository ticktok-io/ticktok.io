package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClocksPurger {

    private final ClocksRepository clocksRepository;
    private final TickChannelExplorer tickChannelExplorer;

    public ClocksPurger(ClocksRepository clocksRepository,
                        TickChannelExplorer tickChannelExplorer) {
        this.clocksRepository = clocksRepository;
        this.tickChannelExplorer = tickChannelExplorer;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void purge() {
        clocksRepository.findByStatus(Clock.ACTIVE).forEach(this::deleteRedundantSchedules);
    }

    private void deleteRedundantSchedules(Clock clock) {
        if (isChannelNotExistsFor(clock)) {
            clocksRepository.deleteClock(clock);
            log.info("Purged the clock: {} with schedule: {}", clock.getName(), clock.getSchedule());
        }
    }

    private boolean isChannelNotExistsFor(Clock clock) {
        return !tickChannelExplorer.isExists(clock);
    }

}
