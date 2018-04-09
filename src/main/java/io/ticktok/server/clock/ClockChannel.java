package io.ticktok.server.clock;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ClockChannel {
    public static final String EXCHANGE_NAME = "ticktok.clock.exchange";

    private String uri;
    private String exchange = EXCHANGE_NAME;
    private String topic;

    public ClockChannel(String uri, String topic) {
        this.uri = uri;
        this.topic = topic;
    }
}
