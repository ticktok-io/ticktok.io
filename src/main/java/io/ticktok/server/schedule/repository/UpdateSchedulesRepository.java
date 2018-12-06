package io.ticktok.server.schedule.repository;

import io.ticktok.server.clock.ScheduleCount;

public interface UpdateSchedulesRepository {

    void addSchedule(String schedule);

    void removeSchedule(String schedule);

    void updateNextTick(String id, long nextTick);

    void saveScheduleGroup(ScheduleCount scheduleCount);
}
