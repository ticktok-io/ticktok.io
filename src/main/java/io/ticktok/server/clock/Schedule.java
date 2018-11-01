package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleParser;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
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
    private long latestScheduledTick;

    public Schedule(String schedule, long latestScheduledTick) {
        this.schedule = schedule;
        this.latestScheduledTick = latestScheduledTick;
    }

    public static Schedule createFrom(Clock clock, long currentTime) {
        return new Schedule(clock.getSchedule(), currentTime);
    }

    public long nextTick() {
        return latestScheduledTick + new ScheduleParser(schedule).interval() * 1000;
    }
}
