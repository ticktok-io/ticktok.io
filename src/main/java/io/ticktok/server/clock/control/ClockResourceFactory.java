package io.ticktok.server.clock.control;

import io.ticktok.server.clock.Clock;
import io.ticktok.server.clock.actions.ClockActionFactory;
import io.ticktok.server.tick.ChannelConnectionInfo;

import static io.ticktok.server.clock.control.HttpRequestUtil.host;

public class ClockResourceFactory {

    private final ClockActionFactory clockActionFactory;

    public ClockResourceFactory(ClockActionFactory actionFactory) {
        this.clockActionFactory = actionFactory;
    }

    public ClockResource create(Clock clock) {
        return createClockResourceBuilderFor(clock)
                .build();
    }

    private ClockResource.ClockResourceBuilder createClockResourceBuilderFor(Clock clock) {
        return ClockResource.builder()
                .domain(host())
                .clock(clock)
                .actions(clockActionFactory.availableActionsFor(clock));
    }

    public ClockResource createWithChannel(Clock clock, ChannelConnectionInfo channel) {
        return createClockResourceBuilderFor(clock)
                .channel(channel)
                .build();
    }

}
