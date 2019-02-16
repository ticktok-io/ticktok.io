package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;

import java.util.List;

public interface HttpQueuesRepository {
    List<TickMessage> pop(String id);

    HttpQueue createQueue(String name, String schedule);

    void push(TickMessage tickMessage);

    void deleteQueue(String queueName);

    boolean isQueueExists(String queueName);

    void updateQueueSchedule(String queueName, String schedule);

    class QueueNotExistsException extends RuntimeException {
        public QueueNotExistsException(String message) {
            super(message);
        }
    }
}
