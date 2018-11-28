package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ScheduleUpdaterAspect {


    private final SchedulesRepository schedulesRepository;

    public ScheduleUpdaterAspect(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @After("execution(* io.ticktok.server.clock.repository.UpdateClockRepository.saveClock(..)) && args(name, schedule)")
    public void addSchedule(JoinPoint joinPoint, String name, String schedule) {
        schedulesRepository.addSchedule(schedule);
    }

    @After("execution(* io.ticktok.server.clock.repository.UpdateClockRepository.deleteClock(..)) && args(clock)")
    public void removeSchedule(JoinPoint joinPoint, Clock clock) {
        schedulesRepository.removeSchedule(clock.getSchedule());
    }
}
