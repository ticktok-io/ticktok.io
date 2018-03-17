package io.ticktok.server;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Clock {

    @Id
    private String id;
    private String schedule;
    private String consumerId;

}
