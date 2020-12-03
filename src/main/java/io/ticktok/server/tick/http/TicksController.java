package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Profile("http")
@RestController
public class TicksController {


    @PostMapping("/api/v1/ticks/poll")
    public List<TickMessage> poll(@RequestBody PollRequest request) {
        return request.getClocks().stream().map(c -> new TickMessage(c, "lalala")).collect(toList());
    }
}
