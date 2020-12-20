package io.ticktok.server.tick.httplong;

public interface ChannelsRepository {

    void updateLastPollTime(long timestamp);

    class ChannelNotExistsException extends RuntimeException {
    }
}
