package io.ticktok.server;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.net.URI;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClockResource extends Clock {

    private String url;

    public ClockResource(URI resourceUrl, Clock clock) {
        super(clock.getId(), clock.getSchedule(), null);
        this.url = resourceUrl.toString();
    }

}
