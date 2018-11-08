package io.ticktok.server.clock;

import lombok.*;
import org.springframework.data.annotation.Id;


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
        return new Clock(null, clockRequest.getName(), clockRequest.getSchedule());
    }

}
