package io.ticktok.server.clock;

import io.ticktok.server.tick.TickChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClockResourceWithChannel extends ClockResource {

    private TickChannel channel;

    public ClockResourceWithChannel(String domain, Clock clock, TickChannel channel) {
        super(domain, clock);
        this.channel = channel;
    }
}
