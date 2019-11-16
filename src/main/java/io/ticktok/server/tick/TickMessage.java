package io.ticktok.server.tick;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TickMessage {
    private String schedule;
}
