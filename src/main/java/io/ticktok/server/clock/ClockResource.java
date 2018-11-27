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

    public ClockResource(String domain, Clock clock) {
        super(clock.getId(), clock.getName(), clock.getSchedule());
        this.url = createUriFor(domain, clock);
    }

    private String createUriFor(String domain, Clock clock) {
        return UriComponentsBuilder.fromHttpUrl(domain)
                .path("/api/v1/clocks/{id}")
                .buildAndExpand(clock.getId()).toUriString();
    }

}
