package io.ticktok.server.clock;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ticktok.server.tick.TickChannel;
import lombok.*;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
@Getter
@ToString
public class ClockResourceV2 extends ResourceSupport {

    private String clockId;
    private String name;
    private String schedule;
    private String status;
    private String url;
    private TickChannel channel;

    @JsonProperty("id")
    public String getClockId() {
        return clockId;
    }

    public static ClockResourceBuilder builder() {
        return new ClockResourceBuilder();
    }

    public static class ClockResourceBuilder {
        private Clock clock;
        private TickChannel channel;
        private String domain;

        public ClockResourceBuilder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public ClockResourceBuilder channel(TickChannel channel) {
            this.channel = channel;
            return this;
        }

        public ClockResourceBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public ClockResourceV2 build() {
            return new ClockResourceV2(
                    clock.getId(),
                    clock.getName(),
                    clock.getSchedule(),
                    clock.getStatus(),
                    createSelfUri(),
                    createChannel());
        }

        private String createSelfUri() {
            return UriComponentsBuilder.fromHttpUrl(domain)
                    .path("/api/v1/clocks/{id}")
                    .buildAndExpand(clock.getId()).toUriString();
        }

        private TickChannel createChannel() {
            if(channel == null) {
                return null;
            }
            return TickChannel.builder()
                    .type(channel.getType())
                    .uri(channel.getUri())
                    .queue(channel.getQueue())
                    .details(detailsWithDomain())
                    .build();
        }

        private Map<String, String> detailsWithDomain() {
            return channel.getDetails().entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().replaceAll("\\{domain}", domain)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

}
