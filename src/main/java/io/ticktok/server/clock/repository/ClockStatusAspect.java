package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.aspectj.lang.JoinPoint;
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

    @AfterReturning("execution(* io.ticktok.server.tick.TickChannelOperations.enable(..)) && args(clock)")
    public void activateClock(JoinPoint joinPoint, Clock clock) {
        clocksRepository.updateStatus(clock.getId(), Clock.ACTIVE);
    }

    @AfterReturning("execution(* io.ticktok.server.tick.TickChannelOperations.disable(..)) && args(clock)")
    public void pauseClock(JoinPoint joinPoint, Clock clock) {
        clocksRepository.updateStatus(clock.getId(), Clock.PAUSED);
    }

}
