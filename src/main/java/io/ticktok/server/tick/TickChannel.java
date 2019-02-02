package io.ticktok.server.tick;


import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TickChannel {

    public static final String RABBIT = "rabbit";

    private String type;
    private String uri;
    private String queue;
    private Map<String, String> details;


    public static TickChannel rabbit(String uri, String queue) {
        return new TickChannel(
                RABBIT,
                uri,
                queue,
                ImmutableMap.of("uri", uri, "queue", queue)
        );
    }
}
