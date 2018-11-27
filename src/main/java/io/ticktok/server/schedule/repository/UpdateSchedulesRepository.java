package io.ticktok.server.schedule.repository;

public interface UpdateSchedulesRepository {

    void updateLatestScheduledTick(String id, long time);

    void addSchedule(String schedule);

    void removeSchedule(String schedule);
}
