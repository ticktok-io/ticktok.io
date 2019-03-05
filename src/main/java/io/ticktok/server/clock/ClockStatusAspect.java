package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClockStatusAspect {

    private final ClocksRepository clocksRepository;

    public ClockStatusAspect(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }

    @AfterReturning("execution(* io.ticktok.server.tick.TickChannelExplorer.enable(..)) && args(clock)")
    public void activateClock(JoinPoint joinPoint, Clock clock) {
        clocksRepository.updateStatus(clock.getId(), Clock.ACTIVE);
    }

    @AfterReturning("execution(* io.ticktok.server.tick.TickChannelExplorer.disable(..)) && args(clock)")
    public void pauseClock(JoinPoint joinPoint, Clock clock) {
        clocksRepository.updateStatus(clock.getId(), Clock.PAUSED);
    }

}
