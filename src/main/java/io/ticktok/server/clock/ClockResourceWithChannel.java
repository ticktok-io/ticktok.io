package io.ticktok.server.clock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ticktok.server.tick.TickChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClockResourceWithChannel extends ClockResource {

    private final TickChannel channel;

    public ClockResourceWithChannel(String domain, Clock clock, TickChannel channel) {
        super(domain, clock);
        this.channel = channelWithPlaceHolders(domain, channel);
    }

    private TickChannel channelWithPlaceHolders(String domain, TickChannel channel) {
        return TickChannel.builder()
                .type(channel.getType())
                .details(detailsWith(domain, channel.getDetails()))
                .build();
    }

    private Map<String, String> detailsWith(String domain, Map<String, String> details) {
        return details.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().replaceAll("\\{domain}", domain)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    @Override
    public String getStatus() {
        return super.getStatus();
    }
}
