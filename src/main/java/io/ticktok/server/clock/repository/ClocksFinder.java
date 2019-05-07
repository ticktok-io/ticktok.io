package io.ticktok.server.clock.repository;

import io.ticktok.server.clock.Clock;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClocksFinder {

    private final ClocksRepository repository;
    private final Map<String, String> parameters;

    public ClocksFinder(ClocksRepository repository) {
        this(repository, new HashMap<>());
    }

    public ClocksFinder(ClocksRepository repository, Map<String, String> parameters) {
        this.repository = repository;
        this.parameters = parameters;
    }

    public List<Clock> find() {
        return repository.findByStatusNot(Clock.PENDING);
    }

    public Clock findById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ClockNotFoundException("Failed to find clock with id: " + id));
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Clock not found")
    public static class ClockNotFoundException extends RuntimeException {

        public ClockNotFoundException(String message) {
            super(message);
        }
    }
}
