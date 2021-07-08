package io.ticktok.server.tick.httplong;


import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TicksChannel {

    @Id
    private String id;
    private String key;
    private String schedule;

    public TicksChannel(String key, String schedule) {
        this.key = key;
        this.schedule = schedule;
    }
}
