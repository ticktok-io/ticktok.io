package io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;


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

    private String scheduleId;
    private String schedule;
    @Indexed
    private long time;
    @Indexed
    private String status;


    public static Tick create(Schedule schedule) {
        return new Tick(null, schedule.getId(), schedule.getSchedule(), schedule.getNextTick(), PENDING);
    }
}
