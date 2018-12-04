package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
public class ScheduleUpdaterAspect {


    private final SchedulesRepository schedulesRepository;
    private AtomicInteger counter = new AtomicInteger();

    public ScheduleUpdaterAspect(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @AfterReturning(value = "execution(public io.ticktok.server.clock.Clock io.ticktok.server.clock.repository.UpdateClockRepository.saveClock(..)) && args(name, schedule)", argNames = "joinPoint,name,schedule")
    public void addSchedule(JoinPoint joinPoint, String name, String schedule) {
        int count = counter.incrementAndGet();
        log.info(count + ". AFTER saveClock {}", joinPoint.toLongString());
        schedulesRepository.addSchedule(schedule);
        log.info(count + ". AFTER saveClock {}", joinPoint.toLongString());
    }

    @AfterReturning("execution(public void io.ticktok.server.clock.repository.UpdateClockRepository.deleteClock(..)) && args(clock)")
    public void removeSchedule(Clock clock) {
        log.info("AFTER deleteClock for: {}", clock.toString());
        schedulesRepository.removeSchedule(clock.getSchedule());
    }
}
