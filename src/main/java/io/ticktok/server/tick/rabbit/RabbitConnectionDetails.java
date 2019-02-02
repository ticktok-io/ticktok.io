package io.ticktok.server.tick.rabbit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RabbitConnectionDetails {

    private String uri;
    private String queue;
}
