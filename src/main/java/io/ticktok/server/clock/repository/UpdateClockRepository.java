package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;

public interface UpdateClockRepository {

    //TODO: add pending
    Clock saveClock(String name, String schedule);

    void deleteScheduleByIndex(String id, int scheduleIndex);

    void deleteByNoSchedules();
}
