package io.ticktok.server.schedule;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document
public class Schedule {

    @Id
    private String id;
    @Indexed(unique = true)
    private String schedule;
    @Indexed
    private long nextTick;
    @Indexed
    private int clockCount;

    public Schedule(String schedule, long nextTick) {
        this.schedule = schedule;
        this.nextTick = nextTick;
    }

    public Schedule(String schedule, long nextTick, int clockCount) {
        this.schedule = schedule;
        this.nextTick = nextTick;
        this.clockCount = clockCount;
    }

    public static Schedule createFrom(String schedule, long currentTime) {
        return new Schedule(schedule, currentTime, 1);
    }

    public Schedule nextTick() {
        long nextTick = this.nextTick + new ScheduleParser(schedule).interval() * 1000;
        return new Schedule(schedule, nextTick, clockCount);
    }
}
