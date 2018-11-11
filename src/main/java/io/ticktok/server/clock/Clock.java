package io.ticktok.server.clock;

import lombok.*;
import org.bson.BsonValue;
import org.springframework.data.annotation.Id;


@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Clock extends ClockRequest {

    @Id
    private String id;

    public Clock(String id, String name, String schedule) {
        super(schedule, name);
        this.id = id;
    }

    public Clock(String name, String schedule) {
        this(null, name, schedule);
    }

    public static Clock createFrom(ClockRequest clockRequest) {
        return new Clock(clockRequest.getName(), clockRequest.getSchedule());
    }

    public static Clock createFrom(String id, Clock clock) {
        return new Clock(id, clock.getName(), clock.getSchedule());
    }
}
