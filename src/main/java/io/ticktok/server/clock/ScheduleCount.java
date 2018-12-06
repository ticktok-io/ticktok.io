package io.ticktok.server.clock;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ScheduleCount {

    @Indexed(unique = true)
    private String schedule;
    private long count;
}
