package io.ticktok.server.tick;

public interface UpdateTicksRepository {

    void updateTickStatus(String id, String status);
}
