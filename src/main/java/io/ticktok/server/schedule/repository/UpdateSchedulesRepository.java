package io.ticktok.server.schedule.repository;

public interface UpdateSchedulesRepository {

    void addSchedule(String schedule);

    void removeSchedule(String schedule);

    void updateNextTick(String id, long nextTick);

}
