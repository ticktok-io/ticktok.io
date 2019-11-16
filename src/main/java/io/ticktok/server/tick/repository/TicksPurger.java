package io.ticktok.server.tick.repository;

import io.ticktok.server.logging.LogExecutionTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicksPurger {

    private final TicksRepository repository;
    private final int keepCount;

    public TicksPurger(@Value("${ticks.purge.keepCount}") String keepCount, TicksRepository repository) {
        this.repository = repository;
        this.keepCount = Integer.valueOf(keepCount);
    }

    @LogExecutionTime
    @Scheduled(fixedDelayString = "${ticks.purge.interval}")
    public void purge() {
        repository.deletePublishedExceptLastPerSchedule(keepCount);
    }
}
