package io.ticktok.server.clock;

import io.ticktok.server.schedule.ScheduleConstraint;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClockRequest {

    @ScheduleConstraint
    private String schedule;
    @NotEmpty
    private String name;

}
