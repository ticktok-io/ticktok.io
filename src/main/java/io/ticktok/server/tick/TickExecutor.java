package io.ticktok.server.tick;

import io.ticktok.server.tick.repository.TicksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
            tickPublisher.publish(t);
            ticksRepository.updateTickStatus(t.getId(), Tick.PUBLISHED);
        });
    }

    protected long now() {
        return System.currentTimeMillis();
    }
}
