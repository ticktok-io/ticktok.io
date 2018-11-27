package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.rabbit.QueueNameCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


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
        }
    }

    private boolean isChannelNotExistsFor(Clock clock) {
        return !tickChannelExplorer.isExists(
                new QueueNameCreator(clock.getName(), clock.getSchedule()).create());
    }

}
