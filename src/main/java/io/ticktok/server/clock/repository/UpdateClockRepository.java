package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.ScheduleCount;

import java.util.List;

public interface UpdateClockRepository {

    Clock saveClock(String name, String schedule);

    void deleteClock(Clock clock);

    void updateStatus(String id, String status);

    List<ScheduleCount> findByScheduleCount();
}
