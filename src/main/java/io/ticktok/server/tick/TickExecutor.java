package io.ticktok.server.tick;

import io.ticktok.server.tick.repository.TicksRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class TickExecutor {

    private final TicksRepository ticksRepository;
    private final TickPublisher tickPublisher;

    public TickExecutor(TicksRepository ticksRepository, TickPublisher tickPublisher) {
        this.ticksRepository = ticksRepository;
        this.tickPublisher = tickPublisher;
    }

    @Scheduled(fixedRate = 1000)
    public void execute() {
        ticksRepository.findByStatusAndTimeLessThanEqual(Tick.PENDING, now()).forEach(t -> {
            ticksRepository.updateTickStatus(t.getId(), Tick.IN_PROGRESS);
            tickPublisher.publish(t.getClock().getSchedule());
            ticksRepository.updateTickStatus(t.getId(), Tick.PUBLISHED);
        });
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
