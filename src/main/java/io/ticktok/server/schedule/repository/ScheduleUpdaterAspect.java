package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ScheduleUpdaterAspect {


    private final SchedulesRepository schedulesRepository;

    public ScheduleUpdaterAspect(SchedulesRepository schedulesRepository) {
        log.info("Init aspect");
        this.schedulesRepository = schedulesRepository;
    }

    @AfterReturning("execution(public io.ticktok.server.clock.Clock io.ticktok.server.clock.repository.UpdateClockRepository.saveClock(..)) && args(name, schedule)")
    public void addSchedule(JoinPoint joinPoint, String name, String schedule) {
        log.info("AFTER saveClock {}", joinPoint.toLongString());
        schedulesRepository.addSchedule(schedule);
    }

    @AfterReturning("execution(public void io.ticktok.server.clock.repository.UpdateClockRepository.deleteClock(..)) && args(clock)")
    public void removeSchedule(Clock clock) {
        log.info("AFTER deleteClock for: {}", clock.toString());
        schedulesRepository.removeSchedule(clock.getSchedule());
    }
}
