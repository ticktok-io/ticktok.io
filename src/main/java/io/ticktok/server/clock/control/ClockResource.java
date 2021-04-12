package io.ticktok.server.clock.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.ChannelConnectionInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@AllArgsConstructor
@Getter
@ToString
public class ClockResource extends RepresentationModel<ClockResource> {

    private final String clockId;
    private final String name;
    private final String schedule;
    private final String status;
    private final ChannelConnectionInfo channel;

    @JsonProperty("id")
    public String getClockId() {
        return clockId;
    }

    public static ClockResourceBuilder builder() {
        return new ClockResourceBuilder();
    }

    public static class ClockResourceBuilder {
        private Clock clock;
        private ChannelConnectionInfo channel;
        private String domain;
        private List<String> actions;

        public ClockResourceBuilder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public ClockResourceBuilder channel(ChannelConnectionInfo channel) {
            this.channel = channel;
            return this;
        }

        public ClockResourceBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public ClockResourceBuilder actions(List<String> actions) {
            this.actions = actions;
            return this;
        }

        public ClockResource build() {
            final ClockResource clockResource = new ClockResource(
                    clock.getId(),
                    clock.getName(),
                    clock.getSchedule(),
                    clock.getStatus(),
                    createChannel());
            clockResource.add(selfLink());
            clockResource.add(actionLinks());
            return clockResource;
        }

        private Link selfLink() {
            return linkTo(ClockController.class, clock.getId()).withSelfRel();
        }

        private List<Link> actionLinks() {
            if (actions != null) {
                return actions.stream().map(this::actionLinkFor).collect(toList());
            }
            return new ArrayList<>();
        }

        private Link actionLinkFor(String a) {
            return linkTo(ClockController.class, clock.getId()).slash(a).withRel(a);
        }

        private ChannelConnectionInfo createChannel() {
            if (channel == null) {
                return null;
            }
            return ChannelConnectionInfo.builder()
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
