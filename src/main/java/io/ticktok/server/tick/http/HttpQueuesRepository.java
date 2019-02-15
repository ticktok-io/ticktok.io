package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;

import java.util.List;

public interface HttpQueuesRepository {
    List<TickMessage> pop(String clockId);

    HttpQueue createQueue(String name, String schedule);

    void push(TickMessage tickMessage);

    void deleteQueue(String name);


    class QueueNotExistsException extends RuntimeException {
        public QueueNotExistsException(String message) {
            super(message);
        }
    }
}
