package io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.httplong.ChannelsRepository;

import java.util.List;

public class MongoChannelsRepository implements ChannelsRepository {

    @Override
    public void updateLastPollTime(List<String> ids, long timestamp) {
        throw new ChannelNotExistsException();
    }
}
