package io.ticktok.server.tick.httplong;

import io.ticktok.server.clock.Clock;

import java.util.List;

public interface ChannelsRepository {

    void updateLastPollTime(List<String> ids);

    TicksChannel createFor(String clockId, String schedule);

    class ChannelNotExistsException extends RuntimeException {
    }
}
