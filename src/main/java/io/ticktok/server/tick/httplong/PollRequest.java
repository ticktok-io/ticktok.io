package io.ticktok.server.tick.httplong;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.Arrays.asList;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class PollRequest {

    private List<String> channels;

    public PollRequest(String... channles) {
        this.channels = asList(channles);
    }
}
