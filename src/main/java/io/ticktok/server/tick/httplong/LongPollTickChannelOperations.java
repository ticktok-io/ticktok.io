package io.ticktok.server.tick.httplong;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.ChannelConnectionInfo;
import io.ticktok.server.tick.TickChannelOperations;


public class LongPollTickChannelOperations implements TickChannelOperations {


    private final ChannelsRepository channelsRepository;

    public LongPollTickChannelOperations(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    @Override
    public boolean isExists(Clock clock) {
        return false;
    }

    @Override
    public ChannelConnectionInfo create(Clock clock) {
        TicksChannel channel = channelsRepository.createFor(clock.getId(), clock.getSchedule());
        return new ChannelConnectionInfo(
                "http-long",
                ImmutableMap.of("channelId", channel.getKey()),
                "",
                ""
        );
    }

    @Override
    public void disable(Clock clock) {

    }

    @Override
    public void enable(Clock clock) {

    }
}
