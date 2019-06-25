package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;

import java.util.List;
import java.util.Map;

public interface CustomClockRepository {

    Clock saveClock(String name, String schedule);

    void deleteClock(Clock clock);

    void updateStatus(String id, String status);

    List<Clock> findBy(Map<String, String> filter);
}
