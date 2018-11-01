package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleParser;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Clock extends ClockRequest {

    @Id
    private String id;

    public Clock(String id, String consumerId, String schedule) {
        super(schedule, consumerId);
        this.id = id;
    }

    public Clock(String consumerId, String schedule) {
        this(null, consumerId, schedule);
    }

    public static Clock createFrom(ClockRequest clockRequest) {
        return new Clock(null, clockRequest.getConsumerId(), clockRequest.getSchedule());
    }

}
