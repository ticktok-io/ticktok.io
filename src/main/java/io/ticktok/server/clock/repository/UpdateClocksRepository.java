package io.ticktok.server.clock.repository;

public interface UpdateClocksRepository {

    void updateLatestScheduledTick(String id, long time);
}
