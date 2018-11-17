package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;

import java.util.List;

public interface UpdateClockRepository {

    Clock saveClock(String name, String schedule);

    void deleteSchedules(String id, List<String> schedules);
}
