package io.ticktok.server.clock;

import io.ticktok.server.tick.TickChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CreatedClockResource extends ClockResource {

    private TickChannel channel;

    public CreatedClockResource(String domain, Clock clock, TickChannel channel) {
        super(domain, clock);
        this.channel = channel;
    }
}
