package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Profile("http")
public class QueuesController {
    private HttpQueuesRepository httpQueuesRepository;

    public QueuesController(HttpQueuesRepository httpQueuesRepository) {
        this.httpQueuesRepository = httpQueuesRepository;
    }

    @GetMapping(HttpConfiguration.POP_PATH)
    public List<TickMessage> pop(@PathVariable String id) {
        return httpQueuesRepository.pop(id);
    }
}
