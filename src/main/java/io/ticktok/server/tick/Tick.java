package io.ticktok.server.tick;

import io.ticktok.server.clock.Clock;
import lombok.*;
import org.springframework.data.annotation.Id;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Tick {

    public static final String PENDING = "PENDING";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String PUBLISHED = "PUBLISHED";

    @Id
    private String id;

    private Clock clock;
    private long time;
    private String status;


    public static Tick create(Clock clock, long time) {
        return new Tick(null, clock, time, PENDING);
    }
}
