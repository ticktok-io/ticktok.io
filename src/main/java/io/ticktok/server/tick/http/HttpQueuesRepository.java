package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

public interface HttpQueuesRepository {
    List<TickMessage> pop(String externalId);

    void push(TickMessage tickMessage);

    HttpQueue createQueue(String name);

    void deleteQueue(String queueName);

    boolean isQueueExists(String queueName);

    void updateQueueSchedule(String queueName, String schedule);

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Queue not found")
    class QueueNotExistsException extends RuntimeException {
        public QueueNotExistsException(String message) {
            super(message);
        }
    }
}
