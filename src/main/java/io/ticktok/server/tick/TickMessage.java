package io.ticktok.server.tick;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TickMessage {
    private String clockId;
    private String schedule;

    public TickMessage(String schedule) {
        this.schedule = schedule;
        this.clockId = null;
    }
}
