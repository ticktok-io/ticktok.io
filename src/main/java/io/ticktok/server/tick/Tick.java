package io.ticktok.server.tick;

import lombok.*;
import org.springframework.data.annotation.Id;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Tick {

    @Id
    private String id;
    private String clockId;
    private long time;

    public static Tick create(String clockId, long time) {
        return new Tick(null, clockId, time);
    }
}
