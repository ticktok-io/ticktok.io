package io.ticktok.server.tick.httplong;

import java.util.List;

public interface ChannelsRepository {

    void updateLastPollTime(List<String> ids);

    class ChannelNotExistsException extends RuntimeException {
    }
}
