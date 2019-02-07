package io.ticktok.server.tick;


import com.google.common.collect.ImmutableMap;
import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class TickChannel {

    public static final String RABBIT = "rabbit";
    public static final String HTTP = "http";

    private String type;
    private String uri;
    private String queue;
    private Map<String, String> details;

}
