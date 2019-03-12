package io.ticktok.server.tick.http;

import com.google.common.collect.ImmutableMap;
import io.ticktok.server.clock.Clock;
import io.ticktok.server.tick.QueueNameCreator;
import io.ticktok.server.tick.TickChannel;
import io.ticktok.server.tick.TickChannelOperations;

import static io.ticktok.server.tick.http.HttpConfiguration.popPathForId;


public class HttpTickChannelOperations implements TickChannelOperations {

    private final HttpQueuesRepository queuesRepository;

    public HttpTickChannelOperations(HttpQueuesRepository queuesRepository) {
        this.queuesRepository = queuesRepository;
    }

    @Override
    public boolean isExists(Clock clock) {
        return queuesRepository.isQueueExists(queueNameFor(clock));
    }

    private String queueNameFor(Clock clock) {
        return new QueueNameCreator(clock).create();
    }

    @Override
    public TickChannel create(Clock clock) {
        HttpQueue httpQueue = queuesRepository.createQueue(queueNameFor(clock));
        return TickChannel.builder()
                .type(TickChannel.HTTP)
                .details(ImmutableMap.of("path", popPathForId(httpQueue.getId())))
                .build();
    }

    @Override
    public void disable(Clock clock) {
        queuesRepository.updateQueueSchedule(queueNameFor(clock), "");
    }

    @Override
    public void enable(Clock clock) {
        queuesRepository.updateQueueSchedule(queueNameFor(clock), clock.getSchedule());
    }
}
