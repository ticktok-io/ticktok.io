package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleConstraint;
import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClockRequest {

    @ScheduleConstraint
    protected String schedule;

    protected String name;

}
