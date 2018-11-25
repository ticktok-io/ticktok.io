package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import io.ticktok.server.clock.repository.SchedulesRepository;
import io.ticktok.server.tick.TickChannelExplorer;
import io.ticktok.server.tick.rabbit.QueueNameCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ClocksPurger {

    private final SchedulesRepository schedulesRepository;
    private final ClocksRepository clocksRepository;
    private final TickChannelExplorer tickChannelExplorer;

    public ClocksPurger(SchedulesRepository schedulesRepository,
                        ClocksRepository clocksRepository,
                        TickChannelExplorer tickChannelExplorer) {
        this.schedulesRepository = schedulesRepository;
        this.clocksRepository = clocksRepository;
        this.tickChannelExplorer = tickChannelExplorer;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void purge() {
        clocksRepository.findAll().forEach(this::deleteRedundantSchedules);
        clocksRepository.deleteByNoSchedules();
    }

    private void deleteRedundantSchedules(Clock clock) {
        for (int i = 0; i < clock.getSchedules().size(); i++) {
            removeScheduleIfNeeded(clock, i);
        }
    }

    private void removeScheduleIfNeeded(Clock clock, int scheduleIndex) {
        if (isChannelNotExistsFor(clock, scheduleIndex)) {
            clocksRepository.deleteScheduleByIndex(clock.getId(), scheduleIndex);
            schedulesRepository.removeClockFor(clock.getSchedules().get(scheduleIndex));
        }
    }

    private boolean isChannelNotExistsFor(Clock clock, int scheduleIndex) {
        return !tickChannelExplorer.isExists(
                new QueueNameCreator(clock.getName(), clock.getSchedules().get(scheduleIndex)).create());
    }

}
