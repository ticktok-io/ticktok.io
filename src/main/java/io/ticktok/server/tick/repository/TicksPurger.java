package io.ticktok.server.tick.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicksPurger {

    private final TicksRepository repository;

    public TicksPurger(TicksRepository repository) {
        this.repository = repository;
    }

    public void purge() {
        repository.deletePublishedExceptLastPerSchedule(10);
        log.info("Purged published ticks");
    }
}
