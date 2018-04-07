package io.ticktok.server.clock;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClockResource extends Clock {

    private String url;
    private ClockChannel channel;

    public ClockResource(String domain, Clock clock, String uri) {
        super(clock.getId(), clock.getSchedule(), null);
        this.channel = new ClockChannel(uri, clock.getSchedule());
        this.url = UriComponentsBuilder.fromHttpUrl(domain)
                .path("/api/v1/clocks/{id}")
                .buildAndExpand(clock.getId()).toUriString();
    }

}
