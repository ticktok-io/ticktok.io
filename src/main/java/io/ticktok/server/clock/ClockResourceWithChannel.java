package io.ticktok.server.clock;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Override
    public String getStatus() {
        return super.getStatus();
    }
}
