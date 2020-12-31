package io.ticktok.server.tick.httplong;

import java.util.List;

public class MongoChannelsRepository implements ChannelsRepository {

    @Override
    public void updateLastPollTime(List<String> ids) {
        throw new ChannelNotExistsException();
    }
}
