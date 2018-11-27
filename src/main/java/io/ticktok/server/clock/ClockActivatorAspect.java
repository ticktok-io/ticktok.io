package io.ticktok.server.clock;

import io.ticktok.server.clock.repository.ClocksRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class ClockActivatorAspect {

    private final ClocksRepository clocksRepository;

    public ClockActivatorAspect(ClocksRepository clocksRepository) {
        this.clocksRepository = clocksRepository;
    }

    @After("execution(* io.ticktok.server.tick.TickChannelCreator.create(..)) && args(name, schedule)")
    public void activateClock(JoinPoint joinPoint, String name, String schedule) {
        Optional<Clock> clock = clocksRepository.findByNameAndSchedule(name, schedule);
        clock.ifPresent(c -> clocksRepository.updateStatus(c.getId(), Clock.ACTIVE));

    }

}
