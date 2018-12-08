package io.ticktok.server.schedule;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<String> clocks;

    public Schedule(String schedule, long nextTick) {
        this.schedule = schedule;
        this.nextTick = nextTick;
    }

    public Schedule(String schedule, long nextTick, List<String> clocks) {
        this.schedule = schedule;
        this.nextTick = nextTick;
        this.clocks = clocks;
    }

    public Schedule nextTick() {
        long nextTick = this.nextTick + new ScheduleParser(schedule).interval() * 1000;
        return new Schedule(id, schedule, nextTick, clocks);
    }
}
