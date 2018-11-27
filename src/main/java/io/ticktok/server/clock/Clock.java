package io.ticktok.server.clock;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Clock {

    public static final String PENDING = "PENDING";
    public static final String ACTIVE = "ACTIVE";
    @Id
    private String id;

    protected String name;
    private String schedule;
    @Indexed
    private String status;
    private long lastModifiedDate;

    public Clock(String id, String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
        this.id = id;
    }

    public Clock(String name, String schedules) {
        this(null, name, schedules);
    }

}
