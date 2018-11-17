package io.ticktok.server.clock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Arrays;
import java.util.List;


@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Clock {

    @Id
    private String id;
    @JsonIgnore
    private List<String> schedules;
    @Indexed(unique = true)
    protected String name;

    public Clock(String id, String name, String... schedules) {
        this.name = name;
        this.schedules = Arrays.asList(schedules);
        this.id = id;
    }

    public Clock(String name, String schedules) {
        this(null, name, schedules);
    }

    public static Clock createFrom(ClockRequest clockRequest) {
        return new Clock(clockRequest.getName(), clockRequest.getSchedule());
    }

    public static Clock createFrom(String id, Clock clock) {
        return new Clock(id, clock.getName(), clock.getSchedule());
    }

    @JsonProperty
    public String getSchedule() {
        return schedules.get(schedules.size() - 1);
    }
}
