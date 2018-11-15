package io.ticktok.server.clock;

import io.ticktok.server.clock.schedule.ScheduleConstraint;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

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
