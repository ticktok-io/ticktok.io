package io.ticktok.server.clock;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClockDetails {

    private String schedule;
    private String consumerId;

}
