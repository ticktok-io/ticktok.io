package io.ticktok.server.clock.repository;

public interface UpdateSchedulesRepository {

    void updateLatestScheduledTick(String id, long time);
}
