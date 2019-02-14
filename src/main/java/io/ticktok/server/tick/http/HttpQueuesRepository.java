package io.ticktok.server.tick.http;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickMessage;

import java.util.List;

public interface HttpQueuesRepository {
    List<TickMessage> pop(String clockId);

    void add(String schedule);

    void assignClock(String clockId, String schedule);

    void add(List<String> clocksIds, TickMessage tickMessage);

    // void unassignClock(String clockId, String schedule);
}
