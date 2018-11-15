package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Schedule;

public interface UpdateSchedulesRepository {

    void updateLatestScheduledTick(String id, long time);

    void decreaseClockCount(String schedule);

    void increaseClockCount(String schedule);

    void saveSchedule(Schedule schedule);

    void addClockFor(String schedule);

    void removeClockFor(String schedule);
}
