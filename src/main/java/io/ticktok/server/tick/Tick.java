package io.ticktok.server.tick;

import io.ticktok.server.schedule.Schedule;
import io.ticktok.server.schedule.ScheduleParser;
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
    private String schedule;
    @Indexed
    private long time;
    @Indexed
    private String status;

    public static Tick create(Schedule schedule) {
        return new Tick(null, schedule.getSchedule(), schedule.getNextTick(), PENDING);
    }

    public static Tick create(String schedule, long time) {
        return new Tick(null, schedule, time, PENDING);
    }

    public Tick nextTick() {
        long nextTick = this.time + new ScheduleParser(schedule).interval() * 1000;
        return new Tick(null, schedule, nextTick, PENDING);
    }

    public Tick boundTo(long boundTime) {
       return new Tick(id, schedule, time < boundTime ? boundTime : time, status);
    }
}
