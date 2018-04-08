package io.ticktok.server.clock;

import lombok.*;
import org.springframework.data.annotation.Id;


@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Clock extends ClockDetails {

    @Id
    private String id;

    public Clock(String id, String schedule) {
        super(schedule);
        this.id = id;
    }

    public static Clock createFrom(ClockDetails clockDetails) {
        return new Clock(null, clockDetails.getSchedule());
    }
}
