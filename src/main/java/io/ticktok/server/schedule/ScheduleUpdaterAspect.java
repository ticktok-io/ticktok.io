package io.ticktok.server.schedule;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.schedule.repository.SchedulesRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ScheduleUpdaterAspect {

    private final SchedulesRepository schedulesRepository;
    private final java.time.Clock systemClock;

    public ScheduleUpdaterAspect(SchedulesRepository schedulesRepository, java.time.Clock systemClock) {
        this.schedulesRepository = schedulesRepository;
        this.systemClock = systemClock;
    }

    @AfterReturning(value = "execution(* io.ticktok.server.clock.repository.CustomClockRepository.saveClock(..)) && inTicktok()", returning = "clock")
    public void addSchedule(Clock clock) {
        schedulesRepository.addClock(clock, new FirstTickCalculator(systemClock).calcFor(clock));
    }

    @Pointcut("within(io.ticktok.server..*)")
    private void inTicktok() {
        // Needed for package scoping
    }

    @AfterReturning(value = "execution(* io.ticktok.server.clock.repository.CustomClockRepository.deleteClock(..)) && args(clock) && inTicktok()", argNames = "clock")
    public void removeSchedule(Clock clock) {
        schedulesRepository.removeClock(clock);
    }

}
