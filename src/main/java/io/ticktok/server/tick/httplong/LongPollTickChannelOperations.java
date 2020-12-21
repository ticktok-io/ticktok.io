package io.ticktok.server.tick.httplong;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueName;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelOperations;
import io.ticktok.server.tick.http.HttpQueue;
import io.ticktok.server.tick.http.HttpQueuesRepository;

import static io.ticktok.server.tick.http.HttpConfiguration.popPathForId;


public class LongPollTickChannelOperations implements TickChannelOperations {


    @Override
    public boolean isExists(Clock clock) {
        return false;
    }

    @Override
    public TickChannel create(Clock clock) {
        return new TickChannel(
                "http-long",
                ImmutableMap.of("channelId", "123"),
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
