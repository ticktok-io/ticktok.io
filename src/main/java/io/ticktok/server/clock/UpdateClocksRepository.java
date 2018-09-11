package io.ticktok.server.clock;

public interface UpdateClocksRepository {

    void updateLatestScheduledTick(String id, long time);
}
