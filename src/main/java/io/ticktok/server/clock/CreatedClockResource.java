package io.ticktok.server.clock;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CreatedClockResource extends ClockResource {

    private ClockChannel channel;

    public CreatedClockResource(String domain, Clock clock, ClockChannel channel) {
        super(domain, clock);
        this.channel = channel;
    }
}
