package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleConstraint;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClockRequest {

    @ScheduleConstraint
    protected String schedule;
    @NotEmpty
    protected String name;

}
