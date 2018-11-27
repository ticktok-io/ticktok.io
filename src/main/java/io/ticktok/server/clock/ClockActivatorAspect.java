package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClockActivatorAspect {

    private final ClocksRepository clocksRepository;

    public ClockActivatorAspect(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }

    @After("execution(* io.ticktok.server.tick.TickChannelExplorer.create(..)) && args(clock)")
    public void activateClock(JoinPoint joinPoint, Clock clock) {
        clocksRepository.updateStatus(clock.getId(), Clock.ACTIVE);
    }

}
