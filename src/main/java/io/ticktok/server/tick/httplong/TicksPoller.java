package io.ticktok.server.tick.httplong;

import io.ticktok.server.tick.TickMessage;

import java.util.List;

public class TicksPoller {

    private final ChannelsRepository channelsRepository;

    public TicksPoller(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    public TickMessage poll(List<String> channelIds) {
        channelsRepository.updateLastPollTime(channelIds, 0L);
        return null;
    }

}
