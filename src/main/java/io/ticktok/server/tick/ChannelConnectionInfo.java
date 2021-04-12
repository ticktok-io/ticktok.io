package io.ticktok.server.tick;


import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class ChannelConnectionInfo {

    public static final String RABBIT = "rabbit";
    public static final String HTTP = "http";

    private String type;
    private Map<String, String> details;

    private String uri;
    private String queue;

}
