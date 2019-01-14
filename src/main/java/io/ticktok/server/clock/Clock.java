package io.ticktok.server.clock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
@CompoundIndexes({
        @CompoundIndex(name = "clock_schedule", def = "{'name' : 1, 'schedule': 1}", unique = true)
})
public class Clock {

    public static final String PENDING = "PENDING";
    public static final String ACTIVE = "ACTIVE";
    public static final String PAUSED = "PAUSED";
    @Id
    private String id;

    private String name;
    private String schedule;
    @Indexed
    private String status;

    @JsonIgnore
    private long lastModifiedDate;

    public Clock(String id, String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
        this.id = id;
    }

    public Clock(String name, String schedules) {
        this(null, name, schedules);
    }

    public Clock(Clock clock) {
        this(clock.getId(), clock.getName(), clock.getSchedule(), clock.getStatus(), clock.getLastModifiedDate());
    }

}
