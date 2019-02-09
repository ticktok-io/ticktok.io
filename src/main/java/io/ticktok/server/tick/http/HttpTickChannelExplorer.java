package io.ticktok.server.tick.http;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelExplorer;

import static io.ticktok.server.tick.http.HttpConfiguration.POP_PATH;


public class HttpTickChannelExplorer implements TickChannelExplorer {

    @Override
    public boolean isExists(Clock clock) {
        return false;
    }

    @Override
    public TickChannel create(Clock clock) {
        return TickChannel.builder()
                .type(TickChannel.HTTP)
                .details(ImmutableMap.of("path", POP_PATH.replaceAll("\\{id}", "123")))
                .build();
    }

    @Override
    public void disable(Clock clock) {

    }

    @Override
    public void enable(Clock clock) {

    }
}
