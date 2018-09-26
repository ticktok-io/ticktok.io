package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleParser;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;


@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Clock extends ClockDetails {

    @Id
    private String id;
    @Indexed
    private Long latestScheduledTick;

    public Clock(String id, String schedule, Long latestScheduledTick) {
        super(schedule);
        this.id = id;
        this.latestScheduledTick = latestScheduledTick;
    }

    public Clock(String id, String schedule) {
        super(schedule);
        this.id = id;
    }


    public static Clock createFrom(ClockDetails clockDetails) {
        return new Clock(null, clockDetails.getSchedule(), System.currentTimeMillis());
    }

    public long nextTick() {
        return latestScheduledTick + new ScheduleParser(schedule).interval() * 1000;
    }
}
