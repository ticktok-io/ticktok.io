package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
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

    public ScheduleUpdaterAspect(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    @AfterReturning(value = "execution(* io.ticktok.server.clock.repository.UpdateClockRepository.saveClock(..)) && args(name, schedule) && inTicktok()", argNames = "name,schedule")
    public void addSchedule(String name, String schedule) {
        log.info("AFTER clockSave");
        schedulesRepository.addSchedule(schedule);
    }

    @Pointcut("within(io.ticktok.server..*)")
    private void inTicktok() {
    }

    @AfterReturning(value = "execution(* io.ticktok.server.clock.repository.UpdateClockRepository.deleteClock(..)) && args(clock) && inTicktok()", argNames = "clock")
    public void removeSchedule(Clock clock) {
        schedulesRepository.removeSchedule(clock.getSchedule());
    }
}
