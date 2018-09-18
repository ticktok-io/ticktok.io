package io.ticktok.server.tick.repository;

public interface UpdateTicksRepository {

    void updateTickStatus(String id, String status);
}
