package io.ticktok.server.tick;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TickChannel {

    private String uri;
    private String exchange;
    private String topic;

}
