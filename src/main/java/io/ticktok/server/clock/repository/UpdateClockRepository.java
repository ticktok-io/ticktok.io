package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;

public interface UpdateClockRepository {

    Clock saveClock(Clock clock);
}
