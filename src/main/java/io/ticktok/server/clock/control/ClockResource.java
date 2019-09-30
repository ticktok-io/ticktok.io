package io.ticktok.server.clock.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.TickChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@AllArgsConstructor
@Getter
@ToString
public class ClockResource extends ResourceSupport {

    private String clockId;
    private String name;
    private String schedule;
    private String status;
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
        private List<String> actions;

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
            return linkTo(methodOn(ClocksController.class).findOne(clock.getId())).withSelfRel();
        }

        private List<Link> actionLinks() {
            if (actions != null) {
                return actions.stream().map(this::actionLinkFor).collect(toList());
            }
            return new ArrayList<>();
        }

        private Link actionLinkFor(String a) {
            return linkTo(methodOn(ClockController.class).clockAction(clock.getId(), a)).withRel(a);
        }

        private TickChannel createChannel() {
            if (channel == null) {
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
