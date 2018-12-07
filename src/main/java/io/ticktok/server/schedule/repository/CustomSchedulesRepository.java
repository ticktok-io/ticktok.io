package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.schedule.Schedule;

import java.util.List;

public interface CustomSchedulesRepository {

    void addClock(Clock clock);

    void removeClock(Clock clock);

    void updateNextTick(String id, long nextTick);

    List<Schedule> findActiveSchedulesByNextTickLesserThan(long time);

    void deleteNonActiveClocks();

}
